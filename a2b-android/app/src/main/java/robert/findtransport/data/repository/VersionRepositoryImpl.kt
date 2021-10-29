package robert.findtransport.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import robert.findtransport.data.api.ApiService
import robert.findtransport.data.model.Result
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.VersionRepository
import robert.findtransport.utils.PREF_VERSION
import robert.findtransport.utils.extensions.makeApiCall
import javax.inject.Inject

class VersionRepositoryImpl @Inject constructor(
  private val apiService: ApiService,
  private val sharedPreferencesService: SharedPreferencesService,
) : VersionRepository {

  override suspend fun getVersionFromApi(): Result<String> =
    makeApiCall { apiService.getVersion() }

  override suspend fun getVersionFromCache(): String =
    withContext(Dispatchers.IO) {
      sharedPreferencesService.getString(PREF_VERSION, "0.0") ?: "0.0"
    }

  override suspend fun cacheVersion(version: String) =
    sharedPreferencesService.putString(PREF_VERSION, version)
}
