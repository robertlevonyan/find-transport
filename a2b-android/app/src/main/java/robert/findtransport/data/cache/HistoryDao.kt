package robert.findtransport.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import robert.findtransport.data.entity.History

@Dao
interface HistoryDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(history: History)

  @Query("SELECT * FROM History ORDER BY timestamp DESC")
  fun getHistory(): List<History>

  @Query("DELETE FROM History")
  fun clearHistory()

  @Query("DELETE FROM History WHERE id = :id")
  fun removeHistoryItem(id: Int)
}
