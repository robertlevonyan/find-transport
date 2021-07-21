package robert.findtransport.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
  suspend fun subscribeToCurrentLocation(): Flow<Location>

  suspend fun subscribeToLocationUpdates(): Flow<Location>

  fun disconnect()
}
