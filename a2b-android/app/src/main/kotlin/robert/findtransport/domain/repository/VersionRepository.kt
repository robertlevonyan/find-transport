package robert.findtransport.domain.repository

import robert.findtransport.data.model.Result

interface VersionRepository {
    suspend fun getVersionFromApi(): Result<String>

    suspend fun getVersionFromCache(): String

    suspend fun cacheVersion(version: String)
}