package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
class TransportStopJoin(
  @PrimaryKey
  @SerialName("id")
  val id: Int? = null,
  @SerialName("transport_id")
  val transportId: Int? = 0,
  @SerialName("stop_id")
  val stopId: Int? = 0,
  @SerialName("reverse")
  val reverse: Int? = 0,
  @SerialName("position")
  val order: Int? = 0,
)
