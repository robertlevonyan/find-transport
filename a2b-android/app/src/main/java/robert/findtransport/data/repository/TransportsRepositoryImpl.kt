package robert.findtransport.data.repository

import android.util.Log
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import robert.findtransport.data.api.ApiService
import robert.findtransport.data.cache.TransportsDao
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.entity.TransportStopJoin
import robert.findtransport.data.model.Result
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.utils.PREF_JOINS_ERROR
import robert.findtransport.utils.PREF_ONLY_FAVORITES
import robert.findtransport.utils.PREF_TRANSPORTS_ERROR
import robert.findtransport.utils.extensions.makeApiCall
import javax.inject.Inject

class TransportsRepositoryImpl @Inject constructor(
  private val apiService: ApiService,
  private val transportsDao: TransportsDao,
  private val preferencesService: SharedPreferencesService,
) : TransportsRepository {
  override suspend fun getTransportsFromApi(): Result<List<Transport>> =
    makeApiCall { apiService.getTransport() }

  override suspend fun getJoinsFromApi(): Result<List<TransportStopJoin>> =
    makeApiCall { apiService.getJoins() }

  override suspend fun cacheTransports(transports: List<Transport>) {
    val saved = transportsDao.saveTransports(transports)
    Log.d("A2B Transport", "${saved.size}")
  }

  override suspend fun cacheJoins(joins: List<TransportStopJoin>) {
    val saved = transportsDao.saveJoins(joins)
    Log.d("A2B Join", "${saved.size}")
  }

  override fun getBusesPaged(): PagingSource<Int, Transport> =
    transportsDao.getBusesPaged()

  override fun getMicrobusesPaged(): PagingSource<Int, Transport> =
    transportsDao.getMicrobusesPaged()

  override fun getTrolleybusesPaged(): PagingSource<Int, Transport> =
    transportsDao.getTrolleybusesPaged()

  override fun getMetroPaged(): PagingSource<Int, Transport> =
    transportsDao.getMetroPaged()

  override fun getTransportById(id: Int): Flow<Transport> =
    transportsDao.getTransportById(id)
      .distinctUntilChanged()

  override suspend fun getTransportsForStop(id: Int): List<Transport> =
    transportsDao.getTransportsForStop(id)

  override fun getTransportStops(transportId: Int?): List<Stop> =
    transportId?.let { id -> transportsDao.getTransportStops(id) } ?: emptyList()

  override fun getTransportStopsReversed(transportId: Int?): List<Stop> =
    transportId?.let { id -> transportsDao.getTransportStopsReversed(id) } ?: emptyList()

  override suspend fun changeFavorite(id: Int, favorite: Boolean) {
    transportsDao.changeFavorite(id, favorite)
  }

  override var areTransportsCached: Boolean
    get() = preferencesService.getBoolean(PREF_TRANSPORTS_ERROR, false)
    set(value) = preferencesService.putBoolean(PREF_TRANSPORTS_ERROR, value)

  override var areJoinsCached: Boolean
    get() = preferencesService.getBoolean(PREF_JOINS_ERROR, false)
    set(value) = preferencesService.putBoolean(PREF_JOINS_ERROR, value)

  override var showOnlyFavorites: Boolean
    get() = preferencesService.getBoolean(PREF_ONLY_FAVORITES, false)
    set(value) = preferencesService.putBoolean(PREF_ONLY_FAVORITES, value)
}
