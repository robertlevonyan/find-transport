package robert.findtransport.domain.repository

interface DatabaseRepository {
    suspend fun getTransportsCount(): Int

    suspend fun getJoinsCount(): Int

    suspend fun getStopsCount(): Int

    suspend fun getLocationsCount(): Int

    suspend fun clearDb()
}
