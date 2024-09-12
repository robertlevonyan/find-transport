package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
class StopLocation(
    @PrimaryKey
    @SerialName("id")
    val id: Int? = null,
    @SerialName("stop_id")
    val stopId: Int? = null,
    @SerialName("lat")
    val lat: Double? = null,
    @SerialName("lng")
    val lng: Double? = null,
)
