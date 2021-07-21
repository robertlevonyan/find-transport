package robert.findtransport.domain.repository

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.entity.TransportStopJoin
import robert.findtransport.data.model.Result

interface TransportsRepository {
  suspend fun getTransportsFromApi(): Result<List<Transport>>

  suspend fun getJoinsFromApi(): Result<List<TransportStopJoin>>

  suspend fun cacheTransports(transports: List<Transport>)

  suspend fun cacheJoins(joins: List<TransportStopJoin>)

//  fun getAllTransports(favorite: Boolean): Flow<List<Transport>>
  fun getAllTransports(favorite: Boolean): List<Transport>

  fun getTransportById(id: Int): Flow<Transport>

  suspend fun getTransportsForStop(id: Int): List<Transport>

  fun getTransportStops(transportId: Int): List<Stop>

  fun getTransportStopsReversed(transportId: Int): List<Stop>

  suspend fun getTransportRoute(coordinates: MutableList<Point>): Flow<Result<DirectionsRoute>>

  suspend fun changeFavorite(id: Int, favorite: Boolean)

  var areTransportsCached: Boolean

  var areJoinsCached: Boolean

  var showOnlyFavorites: Boolean
}
