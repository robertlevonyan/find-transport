package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class TransportStopJoin(
  @PrimaryKey
  @SerializedName("id")
  val id: Int? = null,
  @SerializedName("transport_id")
  val transportId: Int? = 0,
  @SerializedName("stop_id")
  val stopId: Int? = 0,
  @SerializedName("reverse")
  val reverse: Int? = 0,
  @SerializedName("position")
  val order: Int? = 0,
)
