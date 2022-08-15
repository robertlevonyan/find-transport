package robert.findtransport.domain.usecase.stop

import androidx.paging.PagingData
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation

interface StopsUseCase {
  suspend fun getStops(): List<Stop>

  fun getStopsPaged(stop: String, locale: String): Flow<PagingData<Stop>>

  suspend fun getStopsLocations(): List<PointAnnotationOptions>

  suspend fun getMetroStopsLocations(): List<PointAnnotationOptions>

  suspend fun getNearbyStop(stops: List<Stop>, coroutineScope: CoroutineScope): Flow<Stop>

  suspend fun getStop(id: Int): Stop

  suspend fun downloadStops(): Result<Unit>

  suspend fun downloadLocations(): Result<Unit>

  suspend fun getStopCoordinates(stop: Stop): List<StopLocation>

  fun areStopsCached(): Boolean

  fun areLocationsCached(): Boolean
}
