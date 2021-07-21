package robert.findtransport.data.repository

import robert.findtransport.data.api.ApiService
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.service.ResourcesService
import robert.findtransport.domain.repository.FeedbackRepository

class FeedbackRepositoryImpl(
    private val apiService: ApiService,
    private val resourcesService: ResourcesService
) : FeedbackRepository {
  override suspend fun sendFeedback(email: String, subject: String, message: String) {
    apiService.sendFeedback(email, subject, message)
  }
  
  override fun getExceptionMessage(type: ExceptionType): Int =
      resourcesService.getExceptionMessage(type)
}