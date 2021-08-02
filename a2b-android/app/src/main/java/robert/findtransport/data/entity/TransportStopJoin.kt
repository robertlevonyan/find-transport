package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class TransportStopJoin {
  @PrimaryKey
  @SerializedName("id")
  var id: Int? = null

  @SerializedName("transport_id")
  var transportId: Int? = 0

  @SerializedName("stop_id")
  var stopId: Int? = 0

  @SerializedName("reverse")
  var reverse: Int? = 0

  @SerializedName("position")
  var order: Int? = 0
}
