package robert.findtransport.domain.repository

import robert.findtransport.data.model.enums.ExceptionType

interface FeedbackRepository {
  suspend fun sendFeedback(email: String, subject: String, message: String)
  
  fun getExceptionMessage(type: ExceptionType): Int
}