package robert.findtransport.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.URLProtocol
import io.ktor.http.path
import robert.findtransport.data.api.ApiService
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.data.service.ResourcesService
import robert.findtransport.domain.repository.FeedbackRepository
import robert.findtransport.utils.BASE_URL
import robert.findtransport.utils.extensions.getHeader
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
  private val httpClient: HttpClient,
  private val resourcesService: ResourcesService,
) : FeedbackRepository {
  override suspend fun sendFeedback(email: String, subject: String, message: String) {
    try {
      val httpResponse = httpClient.post {
        url {
          protocol = URLProtocol.HTTPS
          host = BASE_URL
          path("a2b/feedb/")
          header("a2bkey", "Bearer ${getHeader()}")
        }
        parameter("mail", email)
        parameter("subject", subject)
        parameter("message", message)
      }
      Result.Success(httpResponse.body<Any>())
    } catch (e: Exception) {
      Log.e("A2B", "ERROR", e)
      Result.Error(A2bException(ExceptionType.API, -1, e))
    }
  }

  override fun getExceptionMessage(type: ExceptionType): Int =
    resourcesService.getExceptionMessage(type)
}
