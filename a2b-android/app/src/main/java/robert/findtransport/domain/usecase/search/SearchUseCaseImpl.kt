package robert.findtransport.domain.usecase.search

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import robert.findtransport.R
import robert.findtransport.data.entity.History
import robert.findtransport.data.model.*
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.domain.mapper.toStopLocation
import robert.findtransport.domain.mapper.toTransport
import robert.findtransport.domain.repository.HistoryRepository
import robert.findtransport.domain.repository.StopsRepository
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.presentation.screens.search.SearchOpenInitiator
import java.util.*
import javax.inject.Inject

class SearchUseCaseImpl @Inject constructor(
  private val stopsRepository: StopsRepository,
  private val transportsRepository: TransportsRepository,
  private val historyRepository: HistoryRepository,
) : SearchUseCase {

  override suspend fun search(fromId: Int, toId: Int, opened: String): Flow<SearchState> = flow {
    emit(SearchState.Searching)

    val from = stopsRepository.getStopById(fromId)?.run {
      toStop(stopsRepository.getStopLocations(fromId).map { it.toStopLocation(this) })
    } ?: throw A2bException(ExceptionType.EMPTY_OR_WRONG_FROM, R.string.error_no_from, Exception(""))
    val to = stopsRepository.getStopById(toId)?.run {
      toStop(stopsRepository.getStopLocations(toId).map { it.toStopLocation(this) })
    } ?: throw A2bException(ExceptionType.EMPTY_OR_WRONG_TO, R.string.error_no_to, Exception(""))
    val saveToHistory = try {
      SearchOpenInitiator.valueOf(opened).let { initiator ->
        when (initiator) {
          SearchOpenInitiator.HOME -> true
          SearchOpenInitiator.HISTORY -> false
        }
      }
    } catch (e: Exception) {
      false
    }

    val fromTransports = transportsRepository.getTransportsForStop(from.id)
    val toTransports = transportsRepository.getTransportsForStop(to.id)

    val foundTransports: List<Transport> = searchTransports(fromTransports, toTransports)

    if (foundTransports.isEmpty()) {
      val stops = stopsRepository.getStopsFromCache().map { stop ->
        stop.toStop(stopsRepository.getStopLocations(stop.id ?: 0).map { location ->
          location.toStopLocation(stop)
        })
      }
      val fromNearby: List<Stop> = getNearbyLimitedFor(from, stops)
      val toNearby: List<Stop> = getNearbyLimitedFor(to, stops)

      val multiResult = mutableListOf<MultiRoute>()

      // get routes from origin point to a nearby point of destination
      val (multiDataTo, interchangeStopTo) = tryFindRoutes(toNearby, fromTransports)
      multiResult.addAll(createToResult(multiDataTo, interchangeStopTo, from, to))
      if (multiResult.isNotEmpty()) {
        checkAndSaveToHistory(saveToHistory, fromId, toId)
        emit(SearchState.Multi(multiResult))
        return@flow
      }

      // get routes from a nearby point of origin to the destination
      val (multiDataFrom, interchangeStopFrom) = tryFindRoutes(fromNearby, toTransports)
      multiResult.addAll(createFromResult(multiDataFrom, interchangeStopFrom, from))
      if (multiResult.isNotEmpty()) {
        checkAndSaveToHistory(saveToHistory, fromId, toId)
        emit(SearchState.Multi(multiResult))
        return@flow
      }

      // get routes from a nearby point of origin to a nearby point of destination
      val fromNearbyAll: List<Stop> = getNearbyFor(from, stops)
      val toNearbyAll: List<Stop> = getNearbyFor(to, stops)

      topFor@ for (i in 1..fromNearbyAll.lastIndex) {
        val fromNear = fromNearbyAll[i]
        for (j in 1..toNearbyAll.lastIndex) {
          val toNear = toNearbyAll[j]
          if (fromNear.id == toNear.id) {
            val from2Transports = transportsRepository.getTransportsForStop(from.id)
            val to2Transports = transportsRepository.getTransportsForStop(toNear.id)
            val found2Transports: List<Transport> = searchTransports(from2Transports, to2Transports)
            if (found2Transports.isNotEmpty()) {
              val from3Transports = transportsRepository.getTransportsForStop(fromNear.id)
              val to3Transports = transportsRepository.getTransportsForStop(to.id)
              val found3Transports: List<Transport> = searchTransports(from3Transports, to3Transports)
              if (found3Transports.isNotEmpty()) {
                multiResult.add(MultiRoute(type = MultiType.TRANSPORT_TITLE, stop = from, case = MultiRouteCase.FROM_TO))
                for (t in found2Transports) {
                  multiResult.add(MultiRoute(type = MultiType.TRANSPORT, transport = t, case = MultiRouteCase.FROM_TO))
                }
                multiResult.add(MultiRoute(type = MultiType.INTERCHANGE_TO, stop = fromNear, case = MultiRouteCase.FROM_TO))
                for (t in found3Transports) {
                  multiResult.add(MultiRoute(type = MultiType.TRANSPORT, transport = t, case = MultiRouteCase.FROM_TO))
                }
                multiResult.add(MultiRoute(type = MultiType.INTERCHANGE_TO, stop = to, case = MultiRouteCase.FROM_TO))
                break@topFor
              }
            }
          }
        }
      }
      checkAndSaveToHistory(saveToHistory, fromId, toId)
      emit(SearchState.Multi(multiResult))
    } else {
      //return routes found
      checkAndSaveToHistory(saveToHistory, fromId, toId)
      emit(SearchState.Single(foundTransports))
    }
  }.flowOn(Dispatchers.IO)

  private fun searchTransports(
    fromT: List<robert.findtransport.data.entity.Transport>,
    toT: List<robert.findtransport.data.entity.Transport>
  ): List<Transport> {
    val data = arrayListOf<Transport>()
    for (apiTransport in fromT) {
      val toFormatted = toT.map { it.id }
      if (toFormatted.contains(apiTransport.id)) {
        data.add(
          apiTransport.toTransport(transportsRepository.getTransportStops(
            apiTransport.id
              ?: 0
          ).map { it.toStop() })
        )
      }
    }
    return data.sortedBy { it.id }
  }

  private suspend fun getNearbyFor(stop: Stop, stops: List<Stop>): List<Stop> = withContext(Dispatchers.IO) {
    val nearby = mutableListOf<Stop>()
    val coordinates = stop.coordinates

    val fromLocation = if (coordinates.isEmpty()) {
      val locations = stopsRepository.getStopLocations(stop.id)
      if (locations.isEmpty()) return@withContext emptyList()
      locations.first().toStopLocation(stop.toApiStop())
    } else {
      coordinates.first()
    }
    nearby.addAll(getNearbyStops(fromLocation, stops))
    return@withContext nearby
  }

  private suspend fun getNearbyLimitedFor(stop: Stop, stops: List<Stop>): List<Stop> = withContext(Dispatchers.IO) {
    val nearby = mutableListOf<Stop>()
    val coordinates = stop.coordinates
    val fromLocation = if (coordinates.isEmpty()) {
      val locations = stopsRepository.getStopLocations(stop.id)
      if (locations.isEmpty()) return@withContext emptyList()
      locations.first().toStopLocation(stop.toApiStop())
    } else {
      coordinates.first()
    }
    nearby.addAll(getNearbyStops(fromLocation, stops).take(5))
    return@withContext nearby
  }

  private fun getNearbyStops(currentStop: StopLocation, stops: List<Stop>): Sequence<Stop> {
    val nearby = mutableListOf<NearbyLocation>()

    for (stop in stops) {
      for (coordinate in stop.coordinates) {
        val newLocation = Location("next").apply {
          latitude = coordinate.lat
          longitude = coordinate.lng
        }

        nearby.add(NearbyLocation(stop.id, newLocation.latitude, newLocation.longitude, Location("stop").apply {
          latitude = currentStop.lat
          longitude = currentStop.lng
        }.distanceTo(newLocation)))
      }
    }

    return nearby.asSequence()
      .sortedBy { it.locationDistance }
      .map { runBlocking { stopsRepository.getStopById(it.stopId)?.toStop() ?: Stop.EMPTY } }
  }

  private suspend fun tryFindRoutes(
    nearby: List<Stop>,
    transports: List<robert.findtransport.data.entity.Transport>,
  ): Pair<Set<Transport>, Stop?> = withContext(Dispatchers.IO) {
    val multiData = mutableSetOf<Transport>()
    var interchangeStop: Stop? = null

    for (stop in nearby) {
      multiData.addAll(searchTransports(transports, transportsRepository.getTransportsForStop(stop.id)))
      if (multiData.isNotEmpty()) {
        interchangeStop = stop
        break
      }
    }

    return@withContext multiData to interchangeStop
  }

  private fun createToResult(
    multiDataTo: Set<Transport>,
    interchangeStopTo: Stop?,
    from: Stop,
    to: Stop,
  ): Collection<MultiRoute> {
    val multiResult = mutableListOf<MultiRoute>()

    if (multiDataTo.isNotEmpty()) {
      multiResult.add(MultiRoute(type = MultiType.TRANSPORT_TITLE, from, case = MultiRouteCase.SINGLE_FROM))
      for (t in multiDataTo) {
        multiResult.add(MultiRoute(type = MultiType.TRANSPORT, transport = t, case = MultiRouteCase.SINGLE_FROM))
      }
      multiResult.add(MultiRoute(type = MultiType.INTERCHANGE_TO, stop = interchangeStopTo, case = MultiRouteCase.SINGLE_FROM))
      multiResult.add(MultiRoute(type = MultiType.WALK_TO, to, case = MultiRouteCase.SINGLE_FROM))
    }
    return multiResult
  }

  private fun createFromResult(
    multiDataFrom: Set<Transport>,
    interchangeStopFrom: Stop?,
    from: Stop,
  ): Collection<MultiRoute> {
    val multiResult = mutableListOf<MultiRoute>()

    if (multiDataFrom.isNotEmpty()) {
      multiResult.add(MultiRoute(type = MultiType.WALK_FROM, from, case = MultiRouteCase.SINGLE_TO))
      multiResult.add(MultiRoute(type = MultiType.INTERCHANGE_FROM, stop = interchangeStopFrom, case = MultiRouteCase.SINGLE_TO))
      for (t in multiDataFrom) {
        multiResult.add(MultiRoute(type = MultiType.TRANSPORT, transport = t, case = MultiRouteCase.SINGLE_TO))
      }
    }
    return multiResult
  }

  private suspend fun checkAndSaveToHistory(saveToHistory: Boolean, fromId: Int, toId: Int) {
    if (saveToHistory) {
      historyRepository.getHistory().firstOrNull()
        ?.any { history ->
          history.fromStopId == fromId && history.toStopId == toId
        }?.let { contains ->
          if (!contains) {
            historyRepository.saveInHistory(
              History(
                fromStopId = fromId,
                toStopId = toId,
                timestamp = Date().time,
              )
            )
          }
        }
    }
  }
}