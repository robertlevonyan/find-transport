package robert.findtransport.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
  suspend fun getCurrentLocation(): Location

  fun subscribeToLocationUpdates(): Flow<Location>
}
