package robert.findtransport.domain.usecase.stop

import android.location.Address
import android.location.Location
import androidx.paging.PagingData
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import robert.findtransport.data.entity.Stop as ApiStop
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation
import robert.findtransport.data.model.StopWithAddress
import robert.findtransport.data.model.enums.NearbyStopStatus

interface StopsUseCase {
  suspend fun getStops(): List<Stop>

  fun getStopsPaged(stop: String, locale: String): Flow<PagingData<Stop>>

  suspend fun getStopsLocations(): List<PointAnnotationOptions>

  suspend fun getMetroStopsLocations(): List<PointAnnotationOptions>

  suspend fun getNearbyStop(location: Location): Stop

  suspend fun getStop(id: Int): Stop

  suspend fun downloadStops(): Result<Unit>

  suspend fun downloadLocations(): Result<Unit>

  suspend fun getStopCoordinates(stop: ApiStop): List<StopLocation>

  fun areStopsCached(): Boolean

  fun areLocationsCached(): Boolean

  suspend fun getAddress(stop: Stop): Address?
}
