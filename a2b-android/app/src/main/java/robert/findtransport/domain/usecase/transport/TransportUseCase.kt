package robert.findtransport.domain.usecase.transport

import android.location.Location
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.SearchState

interface TransportUseCase {
  fun getTransportsPaged(checked: Boolean): Flow<PagingData<Transport>>

  fun getTransportById(id: Int?): Flow<Transport>

  suspend fun getTransportsForStop(id: Int): List<Transport>

  suspend fun downloadTransports(): Result<Unit>

  suspend fun downloadJoins(): Result<Unit>

  fun areTransportsCached(): Boolean

  fun areJoinsCached(): Boolean

  fun getTransportRoute(id: Int, reverse: Boolean, isUnderground: Boolean): Flow<RouteResult>

  suspend fun toggleFavorite(transport: Transport)

  fun getNearbyStopFromTransport(
    transport: Transport,
    start: Stop,
    destination: Stop,
    location: Location,
    coroutineScope: CoroutineScope
  ): Flow<Pair<Stop, Stop>>

  var showOnlyFavorites: Boolean
}
