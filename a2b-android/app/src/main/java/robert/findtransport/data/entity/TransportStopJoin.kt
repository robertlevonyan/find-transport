package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
class TransportStopJoin {
  @PrimaryKey
  @SerialName("id")
  var id: Int? = null

  @SerialName("transport_id")
  var transportId: Int? = 0

  @SerialName("stop_id")
  var stopId: Int? = 0

  @SerialName("reverse")
  var reverse: Int? = 0

  @SerialName("position")
  var order: Int? = 0
}
