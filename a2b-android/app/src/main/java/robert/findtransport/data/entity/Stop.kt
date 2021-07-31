package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable()
class Stop {
  @PrimaryKey
  @SerialName("id")
  var id: Int? = null

  @SerialName("name_am")
  var nameAm: String? = null

  @SerialName("name_ru")
  var nameRu: String? = null

  @SerialName("name_en")
  var nameEn: String? = null
}
