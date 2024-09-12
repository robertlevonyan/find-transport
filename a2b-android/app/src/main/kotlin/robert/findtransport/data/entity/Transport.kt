package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import robert.findtransport.data.cache.TransportRouteTypeConverter

@Entity
@Serializable
class Transport(
    @PrimaryKey
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("vehicle_type")
    val type: Int? = null,
    @TypeConverters(TransportRouteTypeConverter::class)
    @SerialName("route")
    val route: TransportRoute? = null,
    val favorite: Boolean = false,
)

@Serializable
class TransportRoute(
    @SerialName("main_route")
    val mainRoute: List<List<Double>>,
    @SerialName("reversed_route")
    val reversedRoute: List<List<Double>>,
)
