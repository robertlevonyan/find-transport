package robert.findtransport.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.service.FusedLocationService
import robert.findtransport.data.service.LocationObserverService
import robert.findtransport.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
  private val fusedLocationService: FusedLocationService,
  private val locationObserverService: LocationObserverService,
) : LocationRepository {

  override suspend fun getCurrentLocation(): Location =
    fusedLocationService.subscribeToCurrentLocation()

  override fun subscribeToLocationUpdates(): Flow<Location> =
    locationObserverService.getLocationUpdates()
}
