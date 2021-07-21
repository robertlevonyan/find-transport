package robert.findtransport.domain.usecase.feedback

import robert.findtransport.data.model.Result
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.repository.FeedbackRepository
import robert.findtransport.utils.extensions.isEmail
import java.lang.Exception

class FeedbackUseCaseImpl(private val feedbackRepository: FeedbackRepository) : FeedbackUseCase {
  
  override suspend fun sendFeedback(email: String, subject: String, message: String): Result<String> {
    if (email.isEmpty()) {
      return Result.Error(A2bException(ExceptionType.ERROR_EMAIL, feedbackRepository.getExceptionMessage(ExceptionType.EMPTY_EMAIL), Exception("")))
    }
    if (!email.isEmail()) {
      return Result.Error(A2bException(ExceptionType.ERROR_EMAIL, feedbackRepository.getExceptionMessage(ExceptionType.WRONG_EMAIL), Exception("")))
    }
    if (subject.isEmpty()) {
      return Result.Error(A2bException(ExceptionType.ERROR_SUBJECT, feedbackRepository.getExceptionMessage(ExceptionType.ERROR_SUBJECT), Exception("")))
    }
    if (message.isEmpty()) {
      return Result.Error(A2bException(ExceptionType.ERROR_MESSAGE, feedbackRepository.getExceptionMessage(ExceptionType.EMPTY_MESSAGE), Exception("")))
    }
    if (message.length < 20) {
      return Result.Error(A2bException(ExceptionType.ERROR_MESSAGE, feedbackRepository.getExceptionMessage(ExceptionType.SHORT_MESSAGE), Exception("")))
    }
    feedbackRepository.sendFeedback(email, subject, message)
    
    return Result.Success("OK")
  }
}
