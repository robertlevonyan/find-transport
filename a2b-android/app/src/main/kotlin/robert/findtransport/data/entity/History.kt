package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    val fromStopId: Int? = 0,
    val toStopId: Int? = 0,
    val results: Int? = 0,
    val timestamp: Long? = 0,
    val originName: String? = null,
    val originLatitude: Float? = 0f,
    val originLongitude: Float? = 0f,
    val destinationName: String? = null,
    val destinationLatitude: Float? = 0f,
    val destinationLongitude: Float? = 0f,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
