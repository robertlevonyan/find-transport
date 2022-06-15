package robert.findtransport.data.repository

import android.util.Log
import androidx.paging.PagingSource
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.matching.v5.models.MapMatchingResponse
import com.mapbox.geojson.Point
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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
import javax.inject.Inject

class TransportsRepositoryImpl @Inject constructor(
  private val apiService: ApiService,
  private val transportsDao: TransportsDao,
  private val mapboxNavigationService: MapboxNavigationService,
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

  override fun getTransportsPaged(favorite: Boolean): PagingSource<Int, Transport> =
    transportsDao.getTransportsPaged(favorite)

  override fun getTransportById(id: Int): Flow<Transport> =
    transportsDao.getTransportById(id)
      .distinctUntilChanged()

  override suspend fun getTransportsForStop(id: Int): List<Transport> =
    transportsDao.getTransportsForStop(id)

  override fun getTransportStops(transportId: Int?): List<Stop> =
    transportId?.let { id -> transportsDao.getTransportStops(id) } ?: emptyList()

  override fun getTransportStopsReversed(transportId: Int?): List<Stop> =
    transportId?.let { id -> transportsDao.getTransportStopsReversed(id) } ?: emptyList()

  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun getTransportRoute(coordinates: MutableList<Point>): Flow<Result<DirectionsRoute>> = channelFlow {
    if (coordinates.size < 2) {
      if (!channel.isClosedForSend) {
        launch {
          channel.send(
            Result.Error(
              A2bException(
                type = ExceptionType.NAVIGATION_EMPTY,
                errorMessage = R.string.error_no_routes,
                error = Exception("")
              )
            )
          )
        }
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
              launch { channel.send(Result.Success(route)) }
            }
          }
          ?: if (!channel.isClosedForSend) {
            launch {
              channel.send(
                Result.Error(
                  A2bException(
                    type = ExceptionType.NAVIGATION_EMPTY,
                    errorMessage = R.string.error_no_routes,
                    error = Exception("")
                  )
                )
              )
            }
          }
      }

      override fun onFailure(call: Call<MapMatchingResponse>, t: Throwable) {
        Log.e("Navigation", "Error", t)
        if (!channel.isClosedForSend) {
          launch {
            channel.send(
              Result.Error(
                A2bException(
                  type = ExceptionType.NAVIGATION_ERROR,
                  errorMessage = R.string.error_no_routes,
                  error = Exception(t)
                )
              )
            )
          }
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
