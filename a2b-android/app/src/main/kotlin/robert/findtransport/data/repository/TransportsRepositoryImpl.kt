package robert.findtransport.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import robert.findtransport.data.cache.TransportsDao
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.entity.TransportStopJoin
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.utils.BASE_URL
import robert.findtransport.utils.PREF_JOINS_ERROR
import robert.findtransport.utils.PREF_ONLY_FAVORITES
import robert.findtransport.utils.PREF_TRANSPORTS_ERROR
import robert.findtransport.utils.extensions.getHeader
import javax.inject.Inject

class TransportsRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
    private val transportsDao: TransportsDao,
    private val preferencesService: SharedPreferencesService,
) : TransportsRepository {
    override suspend fun getTransportsFromApi(): Result<List<Transport>> =
        try {
            val httpResponse = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path("a2b/newtransport/")
//          path("a2b/newtransport/test")
                    header("a2bkey", "Bearer ${getHeader()}")
                    contentType(Json)
                }
            }
            httpResponse.body<String?>()?.let { jsonString ->
                Result.Success(
                    json.decodeFromString(
                        ListSerializer(Transport.serializer()),
                        jsonString
                    )
                )
            } ?: Result.Error(A2bException(ExceptionType.API, -1, NullPointerException("No Data")))
        } catch (e: Exception) {
            Log.e("A2B", "ERROR", e)
            Result.Error(A2bException(ExceptionType.API, -1, e))
        }

    override suspend fun getJoinsFromApi(): Result<List<TransportStopJoin>> =
        try {
            val httpResponse = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path("a2b/newtsjoin/")
                    header("a2bkey", "Bearer ${getHeader()}")
                }
            }
            httpResponse.body<String?>()?.let { jsonString ->
                Result.Success(
                    json.decodeFromString(
                        ListSerializer(TransportStopJoin.serializer()),
                        jsonString
                    )
                )
            } ?: Result.Error(A2bException(ExceptionType.API, -1, NullPointerException("No Data")))
        } catch (e: Exception) {
            Log.e("A2B", "ERROR", e)
            Result.Error(A2bException(ExceptionType.API, -1, e))
        }

    override suspend fun cacheTransports(transports: List<Transport>) {
        val saved = transportsDao.saveTransports(transports)
        Log.d("A2B Transport", "${saved.size}")
    }

    override suspend fun cacheJoins(joins: List<TransportStopJoin>) {
        val saved = transportsDao.saveJoins(joins)
        Log.d("A2B Join", "${saved.size}")
    }

    override suspend fun getBuses(): List<Transport> =
        transportsDao.getBuses()

    override suspend fun getMicrobuses(): List<Transport> =
        transportsDao.getMicrobuses()

    override suspend fun getTrolleybuses(): List<Transport> =
        transportsDao.getTrolleybuses()

    override suspend fun getMetro(): List<Transport> =
        transportsDao.getMetro()

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
