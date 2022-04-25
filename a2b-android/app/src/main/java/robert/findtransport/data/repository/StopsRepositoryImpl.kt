package robert.findtransport.data.repository

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import robert.findtransport.data.api.ApiService
import robert.findtransport.data.cache.StopsDao
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.StopLocation
import robert.findtransport.data.model.Result
import robert.findtransport.data.service.InMemoryCacheService
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.StopsRepository
import robert.findtransport.utils.MEM_CACHE_STOP
import robert.findtransport.utils.PREF_LOCATIONS_ERROR
import robert.findtransport.utils.PREF_STOPS_ERROR
import robert.findtransport.utils.extensions.makeApiCall
import javax.inject.Inject


class StopsRepositoryImpl @Inject constructor(
  private val apiService: ApiService,
  private val stopsDao: StopsDao,
  private val inMemoryCacheService: InMemoryCacheService,
  private val preferencesService: SharedPreferencesService,
) : StopsRepository {
  override suspend fun getStopsFromApi(): Result<List<Stop>> =
    makeApiCall { apiService.getStops() }

  override suspend fun getStopLocationsFromApi(): Result<List<StopLocation>> =
    makeApiCall { apiService.getStopLocations() }

  override suspend fun cacheStops(stops: List<Stop>) {
    stopsDao.saveStop(stops)
  }

  override suspend fun cacheStopLocations(locations: List<StopLocation>) {
    stopsDao.saveStopLocations(locations)
  }

  override suspend fun getStopsAutocomplete(word: String, field: String): List<Stop> =
    field.takeIf { it.isNotEmpty() }
      ?.let {
        stopsDao.getStopsAutocomplete(SimpleSQLiteQuery("SELECT * FROM Stop WHERE $field LIKE '%$word%'"))
      }
      ?: emptyList()

  override fun saveStopsToInMemoryCache(stops: List<Stop>) =
    inMemoryCacheService.save(MEM_CACHE_STOP to stops)

  override suspend fun getStopsFromCache(): List<Stop> =
    stopsDao.getAllStops()

  override fun getStopsPaged(): PagingSource<Int, Stop> =
    stopsDao.getAllStopsPaged()

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
