package robert.findtransport.domain.usecase.feedback

import robert.findtransport.data.model.Result

interface FeedbackUseCase {
    suspend fun sendFeedback(email: String, subject: String, message: String): Result<String>
}
