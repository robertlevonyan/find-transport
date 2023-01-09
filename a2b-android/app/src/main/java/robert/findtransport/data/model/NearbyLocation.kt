package robert.findtransport.data.model

data class NearbyLocation(
    val stopId: Int,
    val latitude: Double,
    val longitude: Double,
    val locationDistance: Float,
)
