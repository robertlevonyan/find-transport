package robert.findtransport.data.model

data class StopLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val parentStop: Stop = Stop.EMPTY,
)
