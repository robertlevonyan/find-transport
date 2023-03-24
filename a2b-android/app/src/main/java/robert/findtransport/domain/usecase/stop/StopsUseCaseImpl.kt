package robert.findtransport.domain.usecase.stop

import android.location.Address
import android.location.Location
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import robert.findtransport.data.model.*
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.domain.mapper.toStopLocation
import robert.findtransport.domain.repository.LocationRepository
import robert.findtransport.domain.repository.ResourcesRepository
import robert.findtransport.domain.repository.StopsRepository
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.utils.LNG_AM
import robert.findtransport.utils.LNG_RU
import robert.findtransport.utils.STOP_ICON_SIZE
import java.util.*
import javax.inject.Inject
import robert.findtransport.data.entity.Stop as ApiStop

class StopsUseCaseImpl @Inject constructor(
  private val stopsRepository: StopsRepository,
  private val locationRepository: LocationRepository,
  private val resourcesRepository: ResourcesRepository,
  private val permissionUseCase: PermissionUseCase,
) : StopsUseCase {

  override suspend fun getStops(): List<Stop> = withContext(Dispatchers.IO) {
    (stopsRepository.getStopsFromInMemoryCache()
      .takeIf { it.isNotEmpty() }
      ?: run { stopsRepository.getStopsFromCache() })
      .takeIf { it.isNotEmpty() }
      ?.let { cachedStops ->
        cachedStops.map { apiStop ->
          apiStop.toStop(
            stopsRepository.getStopLocations(apiStop.id ?: 0)
              .map { it.toStopLocation(apiStop) })
        }
      }
      ?: emptyList()
  }

  override fun getStopsPaged(stop: String, locale: String): Flow<PagingData<Stop>> =
    Pager(config = PagingConfig(pageSize = 10)) {
      when (locale) {
        LNG_AM -> stopsRepository.getAllStopsPagedAm(stop.replace("'", ""))
        LNG_RU -> stopsRepository.getAllStopsPagedRu(stop.replace("'", ""))
        else -> stopsRepository.getAllStopsPagedEn(stop.replace("'", ""))
      }
    }
      .flow.map { value: PagingData<robert.findtransport.data.entity.Stop> ->
        value.map { apiStop ->
          val currentStop = apiStop.toStop(
            coordinates = getStopCoordinates(apiStop)
          )
//          val address = getAddress(currentStop)
//
//          StopWithAddress(currentStop, address)
          currentStop
        }
      }

  override suspend fun getStopsLocations(): List<PointAnnotationOptions> =
    withContext(Dispatchers.IO) {
      val iconBitmap =
        resourcesRepository.getTransportStopIconBitmap() ?: return@withContext emptyList()
      getStops()
        .asSequence()
        .filter { !it.nameEn.contains("m/s", ignoreCase = true) }
        .flatMap { it.coordinates.asSequence() }
        .map { location ->
          PointAnnotationOptions()
            .withPoint(Point.fromLngLat(location.lng, location.lat))
            .withData(location.parentStop.toApiStop().toJson())
            .withIconSize(STOP_ICON_SIZE)
            .withIconImage(iconBitmap)
        }
        .toList()
    }

  override suspend fun getMetroStopsLocations(): List<PointAnnotationOptions> =
    withContext(Dispatchers.IO) {
      val iconBitmap =
        resourcesRepository.getMetroStopIconBitmap() ?: return@withContext emptyList()
      getStops()
        .asSequence()
        .filter { it.nameEn.contains("m/s", ignoreCase = true) }
        .flatMap { it.coordinates.asSequence() }
        .map { location ->
          PointAnnotationOptions()
            .withPoint(Point.fromLngLat(location.lng, location.lat))
            .withData(location.parentStop.toApiStop().toJson())
            .withIconSize(STOP_ICON_SIZE)
            .withIconImage(iconBitmap)
        }
        .toList()
    }

  override suspend fun getNearbyStop(location: Location): Stop = withContext(Dispatchers.IO) {
    val stops = getStops()

    val nearby = mutableListOf<NearbyLocation>()

    stops.forEach { stop ->
      stop.coordinates.forEach { coordinate ->
        val newLocation = Location("next").apply {
          latitude = coordinate.lat
          longitude = coordinate.lng
        }

        val distance = location.distanceTo(newLocation)

        nearby.add(
          NearbyLocation(
            stopId = stop.id,
            latitude = newLocation.latitude,
            longitude = newLocation.longitude,
            locationDistance = distance,
          )
        )
      }
    }

    nearby.sortBy { it.locationDistance }

    stops.find { stop -> stop.id == nearby.first().stopId } ?: Stop.EMPTY
  }

  override suspend fun getStop(id: Int): Stop = withContext(Dispatchers.IO) {
    stopsRepository.getStopById(id)?.toStop() ?: Stop.EMPTY
  }

  override suspend fun downloadStops(): Result<Unit> = withContext(Dispatchers.IO) {
    when (val apiStopsResult = stopsRepository.getStopsFromApi()) {
      is Result.Success -> {
        stopsRepository.cacheStops(apiStopsResult.data)
        stopsRepository.areStopsCached = true
        Result.Success(Unit)
      }
      is Result.Error -> {
        stopsRepository.cacheStops(emptyList())
        stopsRepository.areStopsCached = false
        apiStopsResult
      }
    }
  }

  override suspend fun downloadLocations(): Result<Unit> = withContext(Dispatchers.IO) {
    when (val apiLocationsResult = stopsRepository.getStopLocationsFromApi()) {
      is Result.Success -> {
        stopsRepository.cacheStopLocations(apiLocationsResult.data)
        stopsRepository.areLocationsCached = true
        Result.Success(Unit)
      }
      is Result.Error -> {
        stopsRepository.cacheStopLocations(emptyList())
        stopsRepository.areLocationsCached = false
        apiLocationsResult
      }
    }
  }

  override suspend fun getStopCoordinates(stop: ApiStop): List<StopLocation> =
    withContext(Dispatchers.IO) {
      stopsRepository.getStopLocations(stop.id).map {
        it.toStopLocation(stop)
      }
    }

  override fun areLocationsCached(): Boolean = stopsRepository.areLocationsCached

  override fun areStopsCached(): Boolean = stopsRepository.areLocationsCached

  override suspend fun getAddress(stop: Stop): Address? {
    val coordinate = stop.coordinates.firstOrNull() ?: return null

    return Location(stop.nameEn).apply {
      latitude = coordinate.lat
      longitude = coordinate.lng
    }.let {
      locationRepository.getAddress(it) ?: Address(Locale.getDefault()).apply {
        latitude = coordinate.lat
        longitude = coordinate.lng
      }
    }
  }
}
