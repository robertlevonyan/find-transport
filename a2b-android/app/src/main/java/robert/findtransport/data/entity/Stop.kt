package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Stop {
  @PrimaryKey
  @SerializedName("id")
  var id: Int? = null

  @SerializedName("name_am")
  var nameAm: String? = null

  @SerializedName("name_ru")
  var nameRu: String? = null

  @SerializedName("name_en")
  var nameEn: String? = null
}
