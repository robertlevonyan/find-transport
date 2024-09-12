package robert.findtransport.domain.usecase.location

import android.location.Address
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.repository.LocationRepository
import robert.findtransport.domain.usecase.stop.StopsUseCase
import javax.inject.Inject

class LocationUseCaseImpl @Inject constructor(
    private val locationRepository: LocationRepository,
    private val stopsUseCase: StopsUseCase,
) :
    LocationUseCase {
    override suspend fun getCurrentLocation(): Address? = withContext(Dispatchers.IO) {
        locationRepository.getCurrentLocation()
    }

    override fun subscribeToLocationUpdates(): Flow<Location> =
        locationRepository.subscribeToLocationUpdates().flowOn(Dispatchers.IO)

    override suspend fun getAddress(latitude: Double?, longitude: Double?): Address? =
        withContext(Dispatchers.IO) {
            if (latitude == null || longitude == null) return@withContext null

            locationRepository.getAddress(Location("point").apply {
                this.latitude = latitude
                this.longitude = longitude
            })
        }

    override suspend fun getNearbyStop(latitude: Double, longitude: Double): Stop? =
        withContext(Dispatchers.IO) {
            stopsUseCase.getNearbyStop(Location("point").apply {
                this.latitude = latitude
                this.longitude = longitude
            })
        }
}
