package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import robert.findtransport.data.cache.TransportRouteTypeConverter

@Entity
class Transport(
  @PrimaryKey
  @SerializedName("id")
  val id: Int? = null,
  @SerializedName("name")
  val name: String? = null,
  @SerializedName("vehicle_type")
  val type: Int? = null,
  @TypeConverters(TransportRouteTypeConverter::class)
  @SerializedName("route")
  val route: TransportRoute? = null,
  val favorite: Boolean = false,
)

class TransportRoute(
  @SerializedName("main_route")
  val mainRoute: List<List<Double>>,
  @SerializedName("reversed_route")
  val reversedRoute: List<List<Double>>,
)
