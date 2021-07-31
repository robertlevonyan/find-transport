package robert.findtransport.domain.usecase.location

import android.location.Location
import kotlinx.coroutines.flow.Flow
import robert.findtransport.domain.repository.LocationRepository

class LocationUseCaseImpl(private val locationRepository: LocationRepository): LocationUseCase {
  override suspend fun subscribeToCurrentLocation(): Flow<Location> =
    locationRepository.subscribeToCurrentLocation()

  override suspend fun subscribeToLocationUpdates(): Flow<Location> =
    locationRepository.subscribeToLocationUpdates()
}
