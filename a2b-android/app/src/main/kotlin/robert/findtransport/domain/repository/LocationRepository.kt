package robert.findtransport.domain.repository

import android.location.Address
import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
  suspend fun getCurrentLocation(): Address?

  suspend fun getAddress(location: Location): Address?

  fun subscribeToLocationUpdates(): Flow<Location>
}
