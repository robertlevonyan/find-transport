package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    var fromStopId: Int? = 0,
    var toStopId: Int? = 0,
    var results: Int? = 0,
    var timestamp: Long? = 0
) {
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0
}