package robert.findtransport.data.repository

import android.location.Address
import android.location.Location
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.service.AddressProviderService
import robert.findtransport.data.service.FusedLocationService
import robert.findtransport.data.service.LocationObserverService
import robert.findtransport.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
  private val fusedLocationService: FusedLocationService,
  private val locationObserverService: LocationObserverService,
  private val addressPrService: AddressProviderService,
) : LocationRepository {

  override suspend fun getCurrentLocation(): Address? =
    fusedLocationService.getCurrentLocationAddress()
      ?.let { addressPrService.getAddress(it) }

  override suspend fun getAddress(location: Location): Address? =
    addressPrService.getAddress(location)

  override fun subscribeToLocationUpdates(): Flow<Location> =
    locationObserverService.getLocationUpdates()
}
