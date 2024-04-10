package robert.findtransport.domain.repository

import androidx.paging.PagingSource
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.StopLocation
import robert.findtransport.data.model.Result

interface StopsRepository {
  suspend fun getStopsFromApi(): Result<List<Stop>>

  suspend fun getStopLocationsFromApi(): Result<List<StopLocation>>

  suspend fun cacheStops(stops: List<Stop>)

  suspend fun cacheStopLocations(locations: List<StopLocation>)

  fun saveStopsToInMemoryCache(stops: List<Stop>)

  suspend fun getStopsFromCache(): List<Stop>

  fun getAllStopsPagedEn(word: String): PagingSource<Int, Stop>

  fun getAllStopsPagedAm(word: String): PagingSource<Int, Stop>

  fun getAllStopsPagedRu(word: String): PagingSource<Int, Stop>

  fun getStopsFromInMemoryCache(): List<Stop>

  suspend fun getStopById(id: Int): Stop?

  suspend fun getStopLocations(stopId: Int?): List<StopLocation>

  suspend fun getStopLocationsFromCache(): List<StopLocation>

  var areStopsCached: Boolean

  var areLocationsCached: Boolean
}