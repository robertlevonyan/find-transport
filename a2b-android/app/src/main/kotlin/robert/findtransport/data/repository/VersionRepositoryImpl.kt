package robert.findtransport.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.VersionRepository
import robert.findtransport.utils.BASE_URL
import robert.findtransport.utils.PREF_VERSION
import robert.findtransport.utils.extensions.getHeader
import javax.inject.Inject

class VersionRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val sharedPreferencesService: SharedPreferencesService,
) : VersionRepository {

    override suspend fun getVersionFromApi(): Result<String> =
        try {
            val httpResponse = httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BASE_URL
                    path("a2b/vernew/")
                    header("a2bkey", "Bearer ${getHeader()}")
                }
            }
            httpResponse.body<String?>()?.let {
                Result.Success(it)
            } ?: Result.Error(A2bException(ExceptionType.API, -1, NullPointerException("No Data")))
        } catch (e: Exception) {
            Log.e("A2B", "ERROR", e)
            Result.Error(A2bException(ExceptionType.API, -1, e))
        }

    override suspend fun getVersionFromCache(): String =
        withContext(Dispatchers.IO) {
            sharedPreferencesService.getString(PREF_VERSION, "0.0") ?: "0.0"
        }

    override suspend fun cacheVersion(version: String) =
        sharedPreferencesService.putString(PREF_VERSION, version)
}
