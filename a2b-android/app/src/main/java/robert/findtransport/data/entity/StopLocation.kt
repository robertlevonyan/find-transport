package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
class StopLocation {
  @PrimaryKey
  @SerialName("id")
  var id: Int? = null

  @SerialName("stop_id")
  var stopId: Int? = null

  @SerialName("lat")
  var lat: Double? = null

  @SerialName("lng")
  var lng: Double? = null
}
