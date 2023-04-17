package robert.findtransport.domain.usecase.location

import android.location.Address
import android.location.Location
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.Stop

interface LocationUseCase {
  suspend fun getCurrentLocation(): Address?

  fun subscribeToLocationUpdates(): Flow<Location>

  suspend fun getAddress(latitude: Double?, longitude: Double?): Address?

  suspend fun getNearbyStop(latitude: Double, longitude: Double): Stop?
}
