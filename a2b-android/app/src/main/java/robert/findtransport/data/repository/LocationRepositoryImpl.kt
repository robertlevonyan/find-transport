package robert.findtransport.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import robert.findtransport.data.service.FusedLocationService
import robert.findtransport.data.service.LocationObserverService
import robert.findtransport.domain.repository.LocationRepository

class LocationRepositoryImpl(
  private val fusedLocationService: FusedLocationService,
  private val locationObserverService: LocationObserverService,
) : LocationRepository {

  override suspend fun subscribeToCurrentLocation(): Flow<Location> =
      fusedLocationService.subscribeToCurrentLocation()

  override suspend fun subscribeToLocationUpdates(): Flow<Location> =
    locationObserverService.getLocationUpdates()
}
