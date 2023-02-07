package robert.findtransport.data.model

data class History(
    val id: Int = 0,
    val fromStop: Stop = Stop.EMPTY,
    val toStop: Stop = Stop.EMPTY,
    val originName: String,
    val originLatitude: Float,
    val originLongitude: Float,
    val destinationName: String,
    val destinationLatitude: Float,
    val destinationLongitude: Float,
    val results: Int = 0,
    val timestamp: Long = 0,
)
