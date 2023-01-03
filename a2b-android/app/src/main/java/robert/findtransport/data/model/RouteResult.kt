package robert.findtransport.data.model

import com.mapbox.api.directions.v5.models.DirectionsRoute

sealed class RouteResult {
  class Success(val route: DirectionsRoute?, val transport: Transport) : RouteResult()
  class Failed(val message: String) : RouteResult()
}
