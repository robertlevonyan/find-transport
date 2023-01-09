package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Stop(
  @PrimaryKey
  @SerializedName("id")
  val id: Int? = null,
  @SerializedName("name_am")
  val nameAm: String? = null,
  @SerializedName("name_ru")
  val nameRu: String? = null,
  @SerializedName("name_en")
  val nameEn: String? = null,
)
