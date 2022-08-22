package robert.findtransport.domain.usecase.transport

import android.location.Location
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mapbox.geojson.Point
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
import robert.findtransport.presentation.compose.screens.search.SearchOpenInitiator
import robert.findtransport.utils.extensions.correctStops
import java.util.*
import javax.inject.Inject
import robert.findtransport.data.entity.Transport as ApiTransport

class TransportUseCaseImpl @Inject constructor(
  private val transportsRepository: TransportsRepository,
  private val stopsRepository: StopsRepository,
  private val historyRepository: HistoryRepository,
) : TransportUseCase {
  override fun getTransportsPaged(checked: Boolean): Flow<PagingData<Transport>> =
    Pager(config = PagingConfig(pageSize = 10)) {
      transportsRepository.getTransportsPaged(checked)
    }.flow.map { value ->
      value.map { apiTransport ->
        apiTransport.toTransport(transportsRepository.getTransportStops(apiTransport.id)
          .map { it.toStop() })
      }
    }

  override fun getTransportById(id: Int): Flow<Transport> =
    transportsRepository.getTransportById(id).map { apiTransport ->
      val stops = transportsRepository.getTransportStops(apiTransport.id).map { apiStop ->
        apiStop.toStop(
          runBlocking {
            stopsRepository.getStopLocations(apiStop.id)
              .map { it.toStopLocation(apiStop) }
          }
        )
      }
      val stopsReversed = transportsRepository.getTransportStopsReversed(apiTransport.id).map { apiStop ->
        apiStop.toStop(
          runBlocking {
            stopsRepository.getStopLocations(apiStop.id)
              .map { it.toStopLocation(apiStop) }
              .reversed()
          }
        )
      }
      apiTransport.toTransport(stops, stopsReversed)
    }.flowOn(Dispatchers.IO)

  override suspend fun getTransportsForStop(id: Int): List<Transport> = withContext(Dispatchers.IO) {
    transportsRepository.getTransportsForStop(id).map { apiTransport ->
      apiTransport
        .toTransport(transportsRepository.getTransportStops(apiTransport.id)
          .map { it.toStop() })
    }
  }

  override suspend fun downloadTransports(): Result<Unit> = withContext(Dispatchers.IO) {
    when (val transportsFromApiResult = transportsRepository.getTransportsFromApi()) {
      is Result.Success -> {
        transportsRepository.cacheTransports(transportsFromApiResult.data)
        transportsRepository.areTransportsCached = true
        Result.Success(Unit)
      }
      is Result.Error -> {
        transportsRepository.cacheTransports(emptyList())
        transportsRepository.areTransportsCached = false
        transportsFromApiResult
      }
    }
  }

  override suspend fun downloadJoins(): Result<Unit> = withContext(Dispatchers.IO) {
    when (val joinsFromApiResult = transportsRepository.getJoinsFromApi()) {
      is Result.Success -> {
        transportsRepository.cacheJoins(joinsFromApiResult.data)
        transportsRepository.areJoinsCached = true
        Result.Success(Unit)
      }
      is Result.Error -> {
        transportsRepository.cacheJoins(emptyList())
        transportsRepository.areJoinsCached = false
        joinsFromApiResult
      }
    }
  }

  override fun areTransportsCached(): Boolean = transportsRepository.areTransportsCached

  override fun areJoinsCached(): Boolean = transportsRepository.areJoinsCached

  override suspend fun searchCheck(from: Stop, to: Stop): Result<Unit> = withContext(Dispatchers.IO) {
    if (from == Stop.EMPTY)
      return@withContext Result.Error(A2bException(ExceptionType.EMPTY_OR_WRONG_FROM, R.string.error_no_from, Exception("")))
    if (to == Stop.EMPTY)
      return@withContext Result.Error(A2bException(ExceptionType.EMPTY_OR_WRONG_TO, R.string.error_no_to, Exception("")))
    if (from.id == to.id)
      return@withContext Result.Error(A2bException(ExceptionType.SAME_STOPS, R.string.error_same_stops, Exception("")))

    return@withContext Result.Success(Unit)
  }

  override suspend fun getTransportRoute(id: Int, reverse: Boolean, isUnderground: Boolean): Flow<Result<RouteResult>> =
    getTransportById(id).map { transport ->
      val stops = if (reverse) transport.stopsReversed else transport.stops
      stops.flatMap { it.coordinates }
        .map { Point.fromLngLat(it.lng, it.lat) }
        .let {
          if (isUnderground) {
            Result.Success(RouteResult(null, transport))
          } else {
            when (val route = transportsRepository.getTransportRoute(it.toMutableList()).first()) {
              is Result.Success -> Result.Success(RouteResult(route.data, transport))
              is Result.Error -> route
            }
          }
        }
    }.flowOn(Dispatchers.IO)

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

  private fun searchTransports(fromT: List<ApiTransport>, toT: List<ApiTransport>): List<Transport> {
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
    val fromLocation = if (stop.coordinates.isEmpty()) {
      val locations = stopsRepository.getStopLocations(stop.id)
      if (locations.isEmpty()) return@withContext emptyList()
      locations.first().toStopLocation(stop.toApiStop())
    } else {
      val coordinates = stop.coordinates
      if (coordinates.isEmpty()) return@withContext emptyList()
      coordinates.first()
    }
    nearby.addAll(getNearbyStops(fromLocation, stops))
    return@withContext nearby
  }

  private suspend fun getNearbyLimitedFor(stop: Stop, stops: List<Stop>): List<Stop> = withContext(Dispatchers.IO) {
    val nearby = mutableListOf<Stop>()
    val fromLocation = if (stop.coordinates.isEmpty()) {
      val locations = stopsRepository.getStopLocations(stop.id)
      if (locations.isEmpty()) return@withContext emptyList()
      locations.first().toStopLocation(stop.toApiStop())
    } else {
      val coordinates = stop.coordinates
      if (coordinates.isEmpty()) return@withContext emptyList()
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
    transports: List<ApiTransport>,
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

  override suspend fun toggleFavorite(transport: Transport) = withContext(Dispatchers.IO) {
    transportsRepository.changeFavorite(transport.id, !transport.isFavorite)
  }

  override fun getNearbyStopFromTransport(
    transport: Transport,
    start: Stop,
    destination: Stop,
    location: Location,
    coroutineScope: CoroutineScope,
  ): Flow<Pair<Stop, Stop>> = flow {
    if (!coroutineScope.coroutineContext.isActive) return@flow

    val nearby = mutableListOf<NearbyLocation>()
    val nearbyDestination = mutableListOf<NearbyLocation>()
    val filledDestination = destination.copy(
      coordinates = stopsRepository.getStopLocations(destination.id).map { it.toStopLocation(destination.toApiStop()) }
    )

    if (filledDestination.coordinates.isEmpty()) return@flow
    val destinationCoordinates = filledDestination.coordinates.first()
    val destinationLocation = Location("destination").apply {
      latitude = destinationCoordinates.lat
      longitude = destinationCoordinates.lng
    }

    val stops = transport.correctStops(start, destination)

    stops.forEach { stop ->
      stop.coordinates.forEach { coordinate ->
        val newLocation = Location("next").apply {
          latitude = coordinate.lat
          longitude = coordinate.lng
        }

        nearby.add(NearbyLocation(stop.id, newLocation.latitude, newLocation.longitude, location.distanceTo(newLocation)))
        nearbyDestination.add(
          NearbyLocation(stop.id, newLocation.latitude, newLocation.longitude, destinationLocation.distanceTo(newLocation))
        )
      }
    }

    if (nearby.isEmpty() || nearbyDestination.isEmpty()) {
      emit(Stop.EMPTY to Stop.EMPTY)
      return@flow
    }

    nearby.sortBy { it.locationDistance }
    nearbyDestination.sortBy { it.locationDistance }

    val nearbyStop = stops.find { stop -> stop.id == nearby.first().stopId } ?: Stop.EMPTY
    val preDestination = stops.findLast { stop -> stop.id == nearbyDestination[1].stopId } ?: Stop.EMPTY

    emit(nearbyStop to preDestination)
  }.flowOn(Dispatchers.IO)

  override var showOnlyFavorites: Boolean = transportsRepository.showOnlyFavorites
}
