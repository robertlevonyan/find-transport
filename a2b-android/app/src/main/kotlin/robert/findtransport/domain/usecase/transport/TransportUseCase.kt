package robert.findtransport.domain.usecase.transport

import android.location.Location
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport

interface TransportUseCase {
  fun getTransportById(id: Int): Flow<Transport>

  fun getBusesPaged(): Flow<PagingData<Transport>>

  fun getMicrobusesPaged(): Flow<PagingData<Transport>>

  fun getTrolleybusesPaged(): Flow<PagingData<Transport>>

  fun getMetroPaged(): Flow<PagingData<Transport>>

  suspend fun getTransportsForStop(id: Int): List<Transport>

  suspend fun downloadTransports(): Result<Unit>

  suspend fun downloadJoins(): Result<Unit>

  fun areTransportsCached(): Boolean

  fun areJoinsCached(): Boolean

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
