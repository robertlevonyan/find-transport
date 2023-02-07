package robert.findtransport.domain.usecase.search

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import robert.findtransport.data.entity.History
import robert.findtransport.data.model.*
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.domain.mapper.toTransport
import robert.findtransport.domain.repository.HistoryRepository
import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.presentation.screens.search.SearchOpenInitiator
import robert.findtransport.utils.extensions.getCurrentName
import java.util.*
import javax.inject.Inject

class SearchUseCaseImpl @Inject constructor(
  private val transportsRepository: TransportsRepository,
  private val historyRepository: HistoryRepository,
  private val localeRepository: LocaleRepository,
  private val stopsUseCase: StopsUseCase,
) : SearchUseCase {

  override suspend fun search(
    originName: String,
    originLatitude: Float,
    originLongitude: Float,
    destinationName: String,
    destinationLatitude: Float,
    destinationLongitude: Float,
    opened: String,
  ): Flow<SearchState> = flow {
    emit(SearchState.Searching)

    val currentLocale = localeRepository.getCurrentLanguage()
    val originLocation = Location(originName).apply {
      latitude = originLatitude.toDouble()
      longitude = originLongitude.toDouble()
    }
    val originStop = stopsUseCase.getNearbyStop(originLocation)
    val destinationLocation = Location(destinationName).apply {
      latitude = destinationLatitude.toDouble()
      longitude = destinationLongitude.toDouble()
    }
    val destinationStop = stopsUseCase.getNearbyStop(destinationLocation)

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

    val fromTransports = transportsRepository.getTransportsForStop(originStop.id)
    val toTransports = transportsRepository.getTransportsForStop(destinationStop.id)

    val foundTransports: List<Transport> = searchTransports(fromTransports, toTransports)

    if (foundTransports.isEmpty()) {
      val stops = stopsUseCase.getStops()
      val fromNearby: List<Stop> = getNearbyLimitedFor(originStop, stops)
      val toNearby: List<Stop> = getNearbyLimitedFor(destinationStop, stops)

      val multiResult = mutableListOf<RouteSearchResult>()

      // get routes from origin point to a nearby point of destination
      val (multiDataTo, interchangeStopTo) = tryFindRoutes(toNearby, fromTransports)
      multiResult.addAll(
        createToResult(
          multiDataTo,
          interchangeStopTo,
          originStop,
          destinationStop
        )
      )
      if (multiResult.isNotEmpty()) {
        checkAndSaveToHistory(
          saveToHistory = saveToHistory,
          originId = originStop.id,
          destinationId = destinationStop.id,
          originName = originName,
          destinationName = destinationName,
          originLatitude = originLatitude,
          originLongitude = originLongitude,
          destinationLatitude = destinationLatitude,
          destinationLongitude = destinationLongitude,
        )
        emit(SearchState.Result(multiResult))
        return@flow
      }

      // get routes from a nearby point of origin to the destination
      val (multiDataFrom, interchangeStopFrom) = tryFindRoutes(fromNearby, toTransports)
      multiResult.addAll(createFromResult(multiDataFrom, interchangeStopFrom, originStop))
      if (multiResult.isNotEmpty()) {
        checkAndSaveToHistory(
          saveToHistory = saveToHistory,
          originId = originStop.id,
          destinationId = destinationStop.id,
          originName = originName,
          destinationName = destinationName,
          originLatitude = originLatitude,
          originLongitude = originLongitude,
          destinationLatitude = destinationLatitude,
          destinationLongitude = destinationLongitude,
        )
        emit(SearchState.Result(multiResult))
        return@flow
      }

      // get routes from a nearby point of origin to a nearby point of destination
      val fromNearbyAll: List<Stop> = getNearbyFor(originStop, stops)
      val toNearbyAll: List<Stop> = getNearbyFor(destinationStop, stops)

      topFor@ for (i in 1..fromNearbyAll.lastIndex) {
        val fromNear = fromNearbyAll[i]
        for (j in 1..toNearbyAll.lastIndex) {
          val toNear = toNearbyAll[j]
          if (fromNear.id == toNear.id) {
            val from2Transports = transportsRepository.getTransportsForStop(originStop.id)
            val to2Transports = transportsRepository.getTransportsForStop(toNear.id)
            val found2Transports: List<Transport> = searchTransports(from2Transports, to2Transports)
            if (found2Transports.isNotEmpty()) {
              val from3Transports = transportsRepository.getTransportsForStop(fromNear.id)
              val to3Transports = transportsRepository.getTransportsForStop(destinationStop.id)
              val found3Transports: List<Transport> =
                searchTransports(from3Transports, to3Transports)
              if (found3Transports.isNotEmpty()) {
                multiResult.add(
                  RouteSearchResult(
                    type = RouteSearchElementType.TRANSPORT_TITLE,
                    stop = originStop,
                    case = RouteSearchCase.FROM_TO
                  )
                )
                for (t in found2Transports) {
                  multiResult.add(
                    RouteSearchResult(
                      type = RouteSearchElementType.TRANSPORT,
                      transport = t,
                      case = RouteSearchCase.FROM_TO
                    )
                  )
                }
                multiResult.add(
                  RouteSearchResult(
                    type = RouteSearchElementType.INTERCHANGE_TO,
                    stop = fromNear,
                    case = RouteSearchCase.FROM_TO
                  )
                )
                for (t in found3Transports) {
                  multiResult.add(
                    RouteSearchResult(
                      type = RouteSearchElementType.TRANSPORT,
                      transport = t,
                      case = RouteSearchCase.FROM_TO
                    )
                  )
                }
                multiResult.add(
                  RouteSearchResult(
                    type = RouteSearchElementType.INTERCHANGE_TO,
                    stop = destinationStop,
                    case = RouteSearchCase.FROM_TO
                  )
                )
                break@topFor
              }
            }
          }
        }
      }
      checkAndSaveToHistory(
        saveToHistory = saveToHistory,
        originId = originStop.id,
        destinationId = destinationStop.id,
        originName = originName,
        destinationName = destinationName,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude,
      )
      emit(SearchState.Result(multiResult))
    } else {
      //return routes found
      checkAndSaveToHistory(
        saveToHistory = saveToHistory,
        originId = originStop.id,
        destinationId = destinationStop.id,
        originName = originName,
        destinationName = destinationName,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude,
      )

      val result = buildList {
        if (originName != originStop.getCurrentName(currentLocale)) {
          add(
            RouteSearchResult(
              type = RouteSearchElementType.WALK_FROM,
              walkDestination = originName,
              case = RouteSearchCase.SINGLE_FROM,
            )
          )
          add(
            RouteSearchResult(
              type = RouteSearchElementType.INTERCHANGE_FROM,
              stop = originStop,
              case = RouteSearchCase.FROM_TO,
            )
          )
        }
        foundTransports.forEach { transport ->
          add(
            RouteSearchResult(
              type = RouteSearchElementType.TRANSPORT,
              transport = transport,
              case = RouteSearchCase.FROM_TO
            )
          )
        }
        add(
          RouteSearchResult(
            type = RouteSearchElementType.INTERCHANGE_TO,
            stop = destinationStop,
            case = RouteSearchCase.SINGLE_TO,
          )
        )
        if (destinationName != destinationStop.getCurrentName(currentLocale)) {
          add(
            RouteSearchResult(
              type = RouteSearchElementType.WALK_TO,
              walkDestination = destinationName,
              case = RouteSearchCase.SINGLE_FROM,
            )
          )
        }
      }
      emit(SearchState.Result(result))
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

  private suspend fun getNearbyFor(stop: Stop, stops: List<Stop>): List<Stop> =
    withContext(Dispatchers.IO) {
      val nearby = mutableListOf<Stop>()
      val coordinates = stop.coordinates
      val fromLocation = coordinates.first()
      nearby.addAll(getNearbyStops(fromLocation, stops))
      return@withContext nearby
    }

  private suspend fun getNearbyLimitedFor(stop: Stop, stops: List<Stop>): List<Stop> =
    withContext(Dispatchers.IO) {
      val nearby = mutableListOf<Stop>()
      val coordinates = stop.coordinates
      val fromLocation = coordinates.first()
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

        nearby.add(
          NearbyLocation(
            stop.id,
            newLocation.latitude,
            newLocation.longitude,
            Location("stop").apply {
              latitude = currentStop.lat
              longitude = currentStop.lng
            }.distanceTo(newLocation)
          )
        )
      }
    }

    return nearby.asSequence()
      .sortedBy { it.locationDistance }
      .map { runBlocking { stopsUseCase.getStop(it.stopId) } }
  }

  private suspend fun tryFindRoutes(
    nearby: List<Stop>,
    transports: List<robert.findtransport.data.entity.Transport>,
  ): Pair<Set<Transport>, Stop?> = withContext(Dispatchers.IO) {
    val multiData = mutableSetOf<Transport>()
    var interchangeStop: Stop? = null

    for (stop in nearby) {
      multiData.addAll(
        searchTransports(
          transports,
          transportsRepository.getTransportsForStop(stop.id)
        )
      )
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
  ): Collection<RouteSearchResult> {
    val multiResult = mutableListOf<RouteSearchResult>()

    if (multiDataTo.isNotEmpty()) {
      multiResult.add(
        RouteSearchResult(
          type = RouteSearchElementType.TRANSPORT_TITLE,
          from,
          case = RouteSearchCase.SINGLE_FROM
        )
      )
      for (t in multiDataTo) {
        multiResult.add(
          RouteSearchResult(
            type = RouteSearchElementType.TRANSPORT,
            transport = t,
            case = RouteSearchCase.SINGLE_FROM
          )
        )
      }
      multiResult.add(
        RouteSearchResult(
          type = RouteSearchElementType.INTERCHANGE_TO,
          stop = interchangeStopTo,
          case = RouteSearchCase.SINGLE_FROM
        )
      )
      multiResult.add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_TO,
          to,
          case = RouteSearchCase.SINGLE_FROM
        )
      )
    }
    return multiResult
  }

  private fun createFromResult(
    multiDataFrom: Set<Transport>,
    interchangeStopFrom: Stop?,
    from: Stop,
  ): Collection<RouteSearchResult> {
    val multiResult = mutableListOf<RouteSearchResult>()

    if (multiDataFrom.isNotEmpty()) {
      multiResult.add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_FROM,
          from,
          case = RouteSearchCase.SINGLE_TO
        )
      )
      multiResult.add(
        RouteSearchResult(
          type = RouteSearchElementType.INTERCHANGE_FROM,
          stop = interchangeStopFrom,
          case = RouteSearchCase.SINGLE_TO
        )
      )
      for (t in multiDataFrom) {
        multiResult.add(
          RouteSearchResult(
            type = RouteSearchElementType.TRANSPORT,
            transport = t,
            case = RouteSearchCase.SINGLE_TO
          )
        )
      }
    }
    return multiResult
  }

  private suspend fun checkAndSaveToHistory(
    saveToHistory: Boolean,
    originId: Int,
    destinationId: Int,
    originName: String,
    destinationName: String,
    originLatitude: Float,
    originLongitude: Float,
    destinationLatitude: Float,
    destinationLongitude: Float,
  ) {
    if (!saveToHistory) return

    historyRepository.getHistory().firstOrNull()
      ?.any { history ->
        history.fromStopId == originId && history.toStopId == destinationId
      }?.let { contains ->
        if (!contains) {
          historyRepository.saveInHistory(
            History(
              fromStopId = originId,
              toStopId = destinationId,
              timestamp = Date().time,
              originName = originName,
              destinationName = destinationName,
              originLatitude = originLatitude,
              originLongitude = originLongitude,
              destinationLatitude = destinationLatitude,
              destinationLongitude = destinationLongitude,
            )
          )
        }
      }
  }
}