package robert.findtransport.domain.usecase.location

import android.location.Address
import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationUseCase {
  suspend fun getCurrentLocation(): Address?

  fun subscribeToLocationUpdates(): Flow<Location>
}
