package robert.findtransport.data.repository

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.matching.v5.models.MapMatchingResponse
import com.mapbox.geojson.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import robert.findtransport.R
import robert.findtransport.data.api.ApiService
import robert.findtransport.data.cache.TransportsDao
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.entity.TransportStopJoin
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.data.service.MapboxNavigationService
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.utils.PREF_JOINS_ERROR
import robert.findtransport.utils.PREF_ONLY_FAVORITES
import robert.findtransport.utils.PREF_TRANSPORTS_ERROR
import robert.findtransport.utils.extensions.makeApiCall
import timber.log.Timber

class TransportsRepositoryImpl(
    private val apiService: ApiService,
    private val transportsDao: TransportsDao,
    private val mapboxNavigationService: MapboxNavigationService,
    private val preferencesService: SharedPreferencesService
) : TransportsRepository {
  override suspend fun getTransportsFromApi(): Result<List<Transport>> =
      makeApiCall { apiService.getTransport() }

  override suspend fun getJoinsFromApi(): Result<List<TransportStopJoin>> =
      makeApiCall { apiService.getJoins() }

  override suspend fun cacheTransports(transports: List<Transport>) =
      println("A2B Transport ${transportsDao.saveTransports(transports).size}")

  override suspend fun cacheJoins(joins: List<TransportStopJoin>) =
      println("A2B Join ${transportsDao.saveJoins(joins).size}")

//  override fun getAllTransports(favorite: Boolean): Flow<List<Transport>> =
//      transportsDao.getAllTransports(favorite)
  override fun getAllTransports(favorite: Boolean): List<Transport> =
      transportsDao.getAllTransports(favorite)

  override fun getTransportById(id: Int): Flow<Transport> =
      transportsDao.getTransportById(id)
          .distinctUntilChanged()

  override suspend fun getTransportsForStop(id: Int): List<Transport> =
      transportsDao.getTransportsForStop(id)

  override fun getTransportStops(transportId: Int): List<Stop> =
      transportsDao.getTransportStops(transportId)

  override fun getTransportStopsReversed(transportId: Int): List<Stop> =
      transportsDao.getTransportStopsReversed(transportId)

  @Suppress("EXPERIMENTAL_API_USAGE")
  override suspend fun getTransportRoute(coordinates: MutableList<Point>): Flow<Result<DirectionsRoute>> = channelFlow {
    if (coordinates.size < 2) {
      if (!channel.isClosedForSend) {
        channel.offer(Result.Error(A2bException(ExceptionType.NAVIGATION_EMPTY, R.string.error_no_routes, Exception(""))))
      }
      return@channelFlow
    }
    val navigation = mapboxNavigationService.getNavigation(coordinates)
    navigation.enqueueCall(object : Callback<MapMatchingResponse> {
      override fun onResponse(call: Call<MapMatchingResponse>, response: Response<MapMatchingResponse>) {
        response.takeIf { it.isSuccessful }
            ?.body()?.matchings()?.run {
              val route = get(0).toDirectionRoute()
              if (!channel.isClosedForSend) {
                channel.offer(Result.Success(route))
              }
              println("Navigation $route")
            }
            ?: if (!channel.isClosedForSend) {
              channel.offer(Result.Error(A2bException(ExceptionType.NAVIGATION_EMPTY, R.string.error_no_routes, Exception(""))))
            }
      }

      override fun onFailure(call: Call<MapMatchingResponse>, t: Throwable) {
        Timber.tag("Navigation").e(t, "Error")
        if (!channel.isClosedForSend) {
          channel.offer(Result.Error(A2bException(ExceptionType.NAVIGATION_ERROR, R.string.error_no_routes, Exception(t))))
        }
      }
    })
//    val directions = mapboxNavigationService.getDirections(coordinates)
//    directions.forEach { direction ->
//          direction.enqueueCall(object : Callback<DirectionsResponse> {
//            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
//              response.takeIf { it.isSuccessful }?.body()?.routes()?.forEach { directionsRoute ->
//                if (!channel.isClosedForSend) {
//                  channel.offer(Result.Success(directionsRoute))
//                }
//              }
//            }
//
//            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
//              Timber.tag("Navigation").e(t, "Error")
//              if (!channel.isClosedForSend) {
//                channel.offer(Result.Error(A2bException(ExceptionType.NAVIGATION_ERROR, R.string.error_no_routes, Exception(t))))
//              }
//            }
//          })
//        }
    awaitClose {
      navigation.cancelCall()
//      directions.forEach {
//        it.cancelCall()
//    }
      println("Closed")
    }
  }

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
