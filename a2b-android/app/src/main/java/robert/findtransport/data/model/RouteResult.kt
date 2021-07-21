package robert.findtransport.data.model

import com.mapbox.api.directions.v5.models.DirectionsRoute

data class RouteResult(
    val route: DirectionsRoute?,
    val transport: Transport
)