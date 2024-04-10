package robert.findtransport.data.model

data class NearbyLocation(
    val stop: Stop,
    val latitude: Double,
    val longitude: Double,
    val locationDistance: Float,
)
