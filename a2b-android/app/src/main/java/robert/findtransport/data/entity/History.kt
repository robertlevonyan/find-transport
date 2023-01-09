package robert.findtransport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
  val fromStopId: Int? = 0,
  val toStopId: Int? = 0,
  val results: Int? = 0,
  val timestamp: Long? = 0,
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0
)
