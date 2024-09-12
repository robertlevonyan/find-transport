package robert.findtransport.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT * FROM Stop WHERE nameEn LIKE '%'||:word||'%' ORDER BY nameEn ASC")
    suspend fun getAllStopsEn(word: String): List<Stop>

    @Query("SELECT * FROM Stop WHERE nameAm LIKE '%'||:word||'%' ORDER BY nameAm ASC")
    suspend fun getAllStopsAm(word: String): List<Stop>

    @Query("SELECT * FROM Stop WHERE nameRu LIKE '%'||:word||'%' ORDER BY nameRu ASC")
    suspend fun getAllStopsRu(word: String): List<Stop>

    @Query("SELECT * FROM StopLocation WHERE stopId = :stopId")
    suspend fun getStopLocations(stopId: Int): List<StopLocation>

    @Query("SELECT * FROM StopLocation")
    suspend fun getAllStopLocation(): List<StopLocation>

    @Query("DELETE FROM Stop")
    suspend fun deleteStops()
}