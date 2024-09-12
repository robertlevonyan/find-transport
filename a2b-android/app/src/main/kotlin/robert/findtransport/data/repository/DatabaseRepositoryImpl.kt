package robert.findtransport.data.repository

import robert.findtransport.data.cache.StopsDao
import robert.findtransport.data.cache.TransportsDao
import robert.findtransport.domain.repository.DatabaseRepository
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val stopsDao: StopsDao,
    private val transportsDao: TransportsDao,
) : DatabaseRepository {

    override suspend fun getTransportsCount(): Int =
        transportsDao.getTransportsCount()

    override suspend fun getJoinsCount(): Int =
        transportsDao.getJoinsCount()

    override suspend fun getStopsCount(): Int =
        stopsDao.getStopsCount()

    override suspend fun getLocationsCount(): Int =
        stopsDao.getLocationsCount()

    override suspend fun clearDb() {
        stopsDao.deleteAllStops()
        stopsDao.deleteAllStopLocations()
        transportsDao.deleteAllJoins()
        transportsDao.deleteAllTransports()
    }
}
