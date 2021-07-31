package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
class Transport {
  @PrimaryKey
  @SerialName("id")
  var id: Int? = null

  @SerialName("name")
  var name: String? = null

  @SerialName("type")
  var type: Int? = null

  @SerialName("isNew")
  var newTransport: Int? = null

  var favorite: Boolean = false
}
