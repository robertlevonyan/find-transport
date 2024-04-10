package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
class Stop(
  @PrimaryKey
  @SerialName("id")
  val id: Int? = null,
  @SerialName("name_am")
  val nameAm: String? = null,
  @SerialName("name_ru")
  val nameRu: String? = null,
  @SerialName("name_en")
  val nameEn: String? = null,
)
