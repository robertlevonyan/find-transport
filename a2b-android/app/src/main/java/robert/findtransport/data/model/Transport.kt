package robert.findtransport.data.model

import com.mapbox.geojson.Point
import robert.findtransport.data.model.enums.TransportType

data class Transport(
    val id: Int = 0,
    val number: String = "",
    val type: TransportType = TransportType.UNDEFINED,
    val stops: List<Stop> = emptyList(),
    val stopsReversed: List<Stop> = emptyList(),
    val route: List<Point> = emptyList(),
    val routeReversed: List<Point> = emptyList(),
    val isFavorite: Boolean = false,
) {
    companion object {
        val EMPTY = Transport()
    }
}
