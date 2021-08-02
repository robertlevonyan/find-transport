package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class StopLocation {
  @PrimaryKey
  @SerializedName("id")
  var id: Int? = null

  @SerializedName("stop_id")
  var stopId: Int? = null

  @SerializedName("lat")
  var lat: Double? = null

  @SerializedName("lng")
  var lng: Double? = null
}
