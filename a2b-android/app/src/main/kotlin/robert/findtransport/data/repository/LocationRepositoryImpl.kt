package robert.findtransport.data.repository

import android.location.Address
import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import robert.findtransport.data.service.AddressProviderService
import robert.findtransport.data.service.FusedLocationService
import robert.findtransport.data.service.LocationObserverService
import robert.findtransport.domain.repository.LocationRepository
import java.util.*
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationService: FusedLocationService,
    private val locationObserverService: LocationObserverService,
    private val addressProviderService: AddressProviderService,
) : LocationRepository {

    override suspend fun getCurrentLocation(): Address? =
        fusedLocationService.getCurrentLocation()
            ?.let { location ->
                addressProviderService.getAddress(location) ?: Address(Locale.getDefault()).apply {
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }

    override suspend fun getAddress(location: Location): Address? =
        addressProviderService.getAddress(location)

    override fun subscribeToLocationUpdates(): Flow<Location> =
        locationObserverService.getLocationUpdates().filterNotNull()
}
