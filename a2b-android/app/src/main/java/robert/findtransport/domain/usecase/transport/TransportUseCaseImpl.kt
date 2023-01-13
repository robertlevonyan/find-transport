package robert.findtransport.domain.usecase.transport

import android.location.Location
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mapbox.geojson.Point
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import robert.findtransport.data.model.*
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.domain.mapper.toStopLocation
import robert.findtransport.domain.mapper.toTransport
import robert.findtransport.domain.repository.StopsRepository
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.utils.extensions.correctStops
import java.util.*
import javax.inject.Inject

class TransportUseCaseImpl @Inject constructor(
  private val transportsRepository: TransportsRepository,
  private val stopsRepository: StopsRepository,
) : TransportUseCase {
  override fun getBusesPaged(): Flow<PagingData<Transport>> =
    Pager(config = PagingConfig(pageSize = 10)) {
      transportsRepository.getBusesPaged()
    }.flow.map { value ->
      value.map { apiTransport ->
        apiTransport.toTransport(transportsRepository.getTransportStops(apiTransport.id)
          .map { it.toStop() })
      }
    }

  override fun getMicrobusesPaged(): Flow<PagingData<Transport>> =
    Pager(config = PagingConfig(pageSize = 10)) {
      transportsRepository.getMicrobusesPaged()
    }.flow.map { value ->
      value.map { apiTransport ->
        apiTransport.toTransport(transportsRepository.getTransportStops(apiTransport.id)
          .map { it.toStop() })
      }
    }

  override fun getTrolleybusesPaged(): Flow<PagingData<Transport>> =
    Pager(config = PagingConfig(pageSize = 10)) {
      transportsRepository.getTrolleybusesPaged()
    }.flow.map { value ->
      value.map { apiTransport ->
        apiTransport.toTransport(transportsRepository.getTransportStops(apiTransport.id)
          .map { it.toStop() })
      }
    }

  override fun getMetroPaged(): Flow<PagingData<Transport>> =
    Pager(config = PagingConfig(pageSize = 10)) {
      transportsRepository.getMetroPaged()
    }.flow.map { value ->
      value.map { apiTransport ->
        apiTransport.toTransport(transportsRepository.getTransportStops(apiTransport.id)
          .map { it.toStop() })
      }
    }

  override fun getTransportById(id: Int?): Flow<Transport> = if (id == null) {
    emptyFlow()
  } else {
    transportsRepository.getTransportById(id).map { apiTransport ->
      val stops = transportsRepository.getTransportStops(apiTransport.id).map { apiStop ->
        apiStop.toStop(
          runBlocking {
            stopsRepository.getStopLocations(apiStop.id)
              .map { it.toStopLocation(apiStop) }
          }
        )
      }
      val stopsReversed =
        transportsRepository.getTransportStopsReversed(apiTransport.id).map { apiStop ->
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
  }

  override suspend fun getTransportsForStop(id: Int): List<Transport> =
    withContext(Dispatchers.IO) {
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

  override fun getTransportRoute(
    id: Int,
    reverse: Boolean,
    isUnderground: Boolean
  ): Flow<RouteResult> =
    getTransportById(id).map { transport ->
      val stops = if (reverse) transport.stopsReversed else transport.stops
      stops.flatMap { it.coordinates }
        .map { Point.fromLngLat(it.lng, it.lat) }
        .let {
          if (isUnderground) {
            RouteResult.Success(null, transport)
          } else {
            when (val route = transportsRepository.getTransportRoute(it.toMutableList()).first()) {
              is Result.Success -> RouteResult.Success(route.data, transport)
              is Result.Error -> RouteResult.Failed(route.exception.message ?: "")
            }
          }
        }
    }.flowOn(Dispatchers.IO)

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
      coordinates = stopsRepository.getStopLocations(destination.id)
        .map { it.toStopLocation(destination.toApiStop()) }
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

        nearby.add(
          NearbyLocation(
            stop.id,
            newLocation.latitude,
            newLocation.longitude,
            location.distanceTo(newLocation)
          )
        )
        nearbyDestination.add(
          NearbyLocation(
            stop.id,
            newLocation.latitude,
            newLocation.longitude,
            destinationLocation.distanceTo(newLocation)
          )
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
    val preDestination =
      stops.findLast { stop -> stop.id == nearbyDestination[1].stopId } ?: Stop.EMPTY

    emit(nearbyStop to preDestination)
  }.flowOn(Dispatchers.IO)

  override var showOnlyFavorites: Boolean = transportsRepository.showOnlyFavorites
}
