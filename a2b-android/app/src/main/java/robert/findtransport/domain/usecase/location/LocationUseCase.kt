package robert.findtransport.domain.usecase.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationUseCase {
  suspend fun subscribeToCurrentLocation(): Flow<Location>

  suspend fun subscribeToLocationUpdates(): Flow<Location>
}
