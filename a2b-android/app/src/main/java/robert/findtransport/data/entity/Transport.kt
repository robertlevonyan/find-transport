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
  val favorite: Boolean = false,
)
