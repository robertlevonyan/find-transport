package robert.findtransport.data.cache

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.entity.TransportStopJoin

@Dao
interface TransportsDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveTransports(transports: List<Transport>): List<Long>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveJoins(transports: List<TransportStopJoin>): List<Long>

  @Query("DELETE FROM Transport")
  fun deleteAllTransports()

  @Query("DELETE FROM TransportStopJoin")
  fun deleteAllJoins()

  @Query("SELECT * FROM Transport WHERE id = :id")
  fun getTransportById(id: Int): Flow<Transport>

  @Query(
    """SELECT * FROM Transport WHERE
    CASE(:favorite) WHEN 1 THEN favorite = 1 ELSE favorite = 1 OR favorite = 0 END 
    ORDER BY type ASC, CAST(name AS DECIMAL) ASC"""
  )
  fun getTransportsPaged(favorite: Boolean): PagingSource<Int, Transport>

  @Query(
    """SELECT * FROM Transport WHERE
    type = 3 OR type = 4 OR type = 5 OR type = 6 OR type = 10 OR type = 11  
    ORDER BY type ASC, CAST(name AS DECIMAL) ASC"""
  )
  fun getBusesPaged(): PagingSource<Int, Transport>

  @Query(
    """SELECT * FROM Transport WHERE
    type = 1 OR type = 2 OR type = 12  
    ORDER BY type ASC, CAST(name AS DECIMAL) ASC"""
  )
  fun getMicrobusesPaged(): PagingSource<Int, Transport>

  @Query(
    """SELECT * FROM Transport WHERE
    type = 7 OR type = 8  
    ORDER BY type ASC, CAST(name AS DECIMAL) ASC"""
  )
  fun getTrolleybusesPaged(): PagingSource<Int, Transport>

  @Query(
    """SELECT * FROM Transport WHERE
    type = 9  
    ORDER BY type ASC, CAST(name AS DECIMAL) ASC"""
  )
  fun getMetroPaged(): PagingSource<Int, Transport>

  @Query("SELECT count(*) FROM Transport")
  suspend fun getTransportsCount(): Int

  @Query("SELECT count(*) FROM TransportStopJoin")
  suspend fun getJoinsCount(): Int

  @Query(
    """SELECT id, name, type, favorite FROM 
    (SELECT transportId FROM TransportStopJoin WHERE stopId = :id) as TransportIds 
    INNER JOIN Transport 
    ON TransportIds.transportId = id
    GROUP BY name, type, favorite
    ORDER BY type ASC, CAST(name AS DECIMAL) ASC"""
  )
  suspend fun getTransportsForStop(id: Int): List<Transport>

  @Query(
    """SELECT Stop.id, nameAm, nameRu, nameEn FROM Stop
    INNER JOIN TransportStopJoin
    ON TransportStopJoin.stopId = Stop.id
    AND TransportStopJoin.transportId = :transportId
    AND TransportStopJoin.reverse = 0
    ORDER BY TransportStopJoin.`order` ASC"""
  )
  fun getTransportStops(transportId: Int): List<Stop>

  @Query(
    """SELECT Stop.id, nameAm, nameRu, nameEn FROM Stop
    INNER JOIN TransportStopJoin
    ON TransportStopJoin.stopId = Stop.id
    AND TransportStopJoin.transportId = :transportId
    AND TransportStopJoin.reverse = 1
    ORDER BY TransportStopJoin.`order` ASC"""
  )
  fun getTransportStopsReversed(transportId: Int): List<Stop>

  @Query("UPDATE Transport SET favorite = :favorite WHERE id = :id")
  suspend fun changeFavorite(id: Int, favorite: Boolean)

}
