package robert.findtransport.domain.repository

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.entity.TransportStopJoin
import robert.findtransport.data.model.Result

interface TransportsRepository {
  suspend fun getTransportsFromApi(): Result<List<Transport>>

  suspend fun getJoinsFromApi(): Result<List<TransportStopJoin>>

  suspend fun cacheTransports(transports: List<Transport>)

  suspend fun cacheJoins(joins: List<TransportStopJoin>)

  fun getBusesPaged(): PagingSource<Int, Transport>

  fun getMicrobusesPaged(): PagingSource<Int, Transport>

  fun getTrolleybusesPaged(): PagingSource<Int, Transport>

  fun getMetroPaged(): PagingSource<Int, Transport>

  fun getTransportById(id: Int): Flow<Transport>

  suspend fun getTransportsForStop(id: Int): List<Transport>

  fun getTransportStops(transportId: Int?): List<Stop>

  fun getTransportStopsReversed(transportId: Int?): List<Stop>

  suspend fun changeFavorite(id: Int, favorite: Boolean)

  var areTransportsCached: Boolean

  var areJoinsCached: Boolean

  var showOnlyFavorites: Boolean
}
