package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Transport {
  @PrimaryKey
  @SerializedName("id")
  var id: Int? = null

  @SerializedName("name")
  var name: String? = null

  @SerializedName("type")
  var type: Int? = null

  @SerializedName("isNew")
  var newTransport: Int? = null

  var favorite: Boolean = false
}
