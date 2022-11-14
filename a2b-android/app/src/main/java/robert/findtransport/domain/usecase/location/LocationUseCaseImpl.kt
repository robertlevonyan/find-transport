package robert.findtransport.domain.usecase.location

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import robert.findtransport.domain.repository.LocationRepository
import javax.inject.Inject

class LocationUseCaseImpl @Inject constructor(private val locationRepository: LocationRepository) : LocationUseCase {
  override suspend fun getCurrentLocation(): Location = withContext(Dispatchers.IO) {
    locationRepository.getCurrentLocation()
  }

  override fun subscribeToLocationUpdates(): Flow<Location> =
    locationRepository.subscribeToLocationUpdates().flowOn(Dispatchers.IO)
}
