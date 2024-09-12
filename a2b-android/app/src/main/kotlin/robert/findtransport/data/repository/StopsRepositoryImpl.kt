package robert.findtransport.data.repository

import android.util.Log
import androidx.paging.PagingSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import robert.findtransport.data.cache.StopsDao
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.StopLocation
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.data.service.InMemoryCacheService
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.StopsRepository
import robert.findtransport.utils.BASE_URL
import robert.findtransport.utils.MEM_CACHE_STOP
import robert.findtransport.utils.PREF_LOCATIONS_ERROR
import robert.findtransport.utils.PREF_STOPS_ERROR
import robert.findtransport.utils.extensions.getHeader
import javax.inject.Inject


class StopsRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
    private val stopsDao: StopsDao,
    private val inMemoryCacheService: InMemoryCacheService,
    private val preferencesService: SharedPreferencesService,
) : StopsRepository {
    override suspend fun getStopsFromApi(): Result<List<Stop>> =
        try {
            val httpResponse = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path("a2b/newstops/")
                    header("a2bkey", "Bearer ${getHeader()}")
                }
            }
            httpResponse.body<String?>()?.let { jsonString ->
                Result.Success(json.decodeFromString(ListSerializer(Stop.serializer()), jsonString))
            } ?: Result.Error(A2bException(ExceptionType.API, -1, NullPointerException("No Data")))
        } catch (e: Exception) {
            Log.e("A2B", "ERROR", e)
            Result.Error(A2bException(ExceptionType.API, -1, e))
        }

    override suspend fun getStopLocationsFromApi(): Result<List<StopLocation>> =
        try {
            val httpResponse = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path("a2b/newlocation/")
                    header("a2bkey", "Bearer ${getHeader()}")
                }
            }
            httpResponse.body<String?>()?.let { jsonString ->
                Result.Success(
                    json.decodeFromString(
                        ListSerializer(StopLocation.serializer()),
                        jsonString
                    )
                )
            } ?: Result.Error(A2bException(ExceptionType.API, -1, NullPointerException("No Data")))
        } catch (e: Exception) {
            Log.e("A2B", "ERROR", e)
            Result.Error(A2bException(ExceptionType.API, -1, e))
        }

    override suspend fun cacheStops(stops: List<Stop>) {
        stopsDao.saveStop(stops)
    }

    override suspend fun cacheStopLocations(locations: List<StopLocation>) {
        stopsDao.saveStopLocations(locations)
    }

    override fun saveStopsToInMemoryCache(stops: List<Stop>) =
        inMemoryCacheService.save(MEM_CACHE_STOP to stops)

    override suspend fun getStopsFromCache(): List<Stop> =
        stopsDao.getAllStops()

    override fun getAllStopsPagedEn(word: String): PagingSource<Int, Stop> =
        stopsDao.getAllStopsPagedEn(word)

    override fun getAllStopsPagedAm(word: String): PagingSource<Int, Stop> =
        stopsDao.getAllStopsPagedAm(word)

    override fun getAllStopsPagedRu(word: String): PagingSource<Int, Stop> =
        stopsDao.getAllStopsPagedRu(word)

    override fun getStopsFromInMemoryCache(): List<Stop> =
        inMemoryCacheService.get<List<Stop>>(MEM_CACHE_STOP) ?: emptyList()

    override suspend fun getStopById(id: Int): Stop? =
        stopsDao.getStopById(id)

    override suspend fun getStopLocations(stopId: Int?): List<StopLocation> =
        stopId?.let { id -> stopsDao.getStopLocations(id) } ?: emptyList()

    override suspend fun getStopLocationsFromCache(): List<StopLocation> =
        stopsDao.getAllStopLocation()

    override var areStopsCached: Boolean
        get() = preferencesService.getBoolean(PREF_STOPS_ERROR, false)
        set(value) {
            preferencesService.putBoolean(PREF_STOPS_ERROR, value)
        }

    override var areLocationsCached: Boolean
        get() = preferencesService.getBoolean(PREF_LOCATIONS_ERROR, false)
        set(value) {
            preferencesService.putBoolean(PREF_LOCATIONS_ERROR, value)
        }
}
