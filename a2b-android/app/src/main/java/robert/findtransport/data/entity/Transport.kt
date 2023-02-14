package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Transport(
  @PrimaryKey
  @SerializedName("id")
  val id: Int? = null,
  @SerializedName("name")
  val name: String? = null,
  @SerializedName("vehicle_type")
  val type: Int? = null,
  @SerializedName("route_main")
  val routeMain: String? = null,
  @SerializedName("route_secondary")
  val routeSecondary: String? = null,
  val favorite: Boolean = false,
)
