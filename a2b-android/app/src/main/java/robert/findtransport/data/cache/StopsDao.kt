package robert.findtransport.data.cache

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.StopLocation

@Dao
interface StopsDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveStop(stops: List<Stop>): List<Long>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveStopLocations(locations: List<StopLocation>): List<Long>

  @Query("DELETE FROM Stop")
  fun deleteAllStops()

  @Query("DELETE FROM StopLocation")
  fun deleteAllStopLocations()

  @Query("SELECT * FROM Stop WHERE id = :id")
  suspend fun getStopById(id: Int): Stop?

  @Query("SELECT * FROM Stop")
  suspend fun getAllStops(): List<Stop>

  @Query("SELECT count(*) FROM Stop")
  suspend fun getStopsCount(): Int

  @Query("SELECT count(*) FROM StopLocation")
  suspend fun getLocationsCount(): Int

  @RawQuery
  suspend fun getStopsAutocomplete(supportSQLiteQuery: SupportSQLiteQuery): List<Stop>

  @Query("SELECT * FROM Stop")
  fun getAllStopsPaged(): PagingSource<Int, Stop>

  @Query("SELECT * FROM StopLocation WHERE stopId = :stopId")
  suspend fun getStopLocations(stopId: Int): List<StopLocation>

  @Query("SELECT * FROM StopLocation")
  suspend fun getAllStopLocation(): List<StopLocation>
}