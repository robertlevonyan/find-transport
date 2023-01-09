package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class StopLocation(
  @PrimaryKey
  @SerializedName("id")
  val id: Int? = null,
  @SerializedName("stop_id")
  val stopId: Int? = null,
  @SerializedName("lat")
  val lat: Double? = null,
  @SerializedName("lng")
  val lng: Double? = null,
)
