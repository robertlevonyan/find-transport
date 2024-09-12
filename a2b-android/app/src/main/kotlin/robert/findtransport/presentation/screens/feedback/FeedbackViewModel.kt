package robert.findtransport.presentation.screens.feedback

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.usecase.feedback.FeedbackUseCase
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(private val feedbackUseCase: FeedbackUseCase) :
    BaseViewModel() {
    val email = MutableStateFlow("")
    val subject = MutableStateFlow("")
    val message = MutableStateFlow("")
    val feedbackProcess = MutableStateFlow<FeedbackSendingStatus>(FeedbackSendingStatus.Idle)

    fun sendFeedback() {
        viewModelScope.launch(Dispatchers.IO) {
            feedbackProcess.value = FeedbackSendingStatus.Sending
            when (val response =
                feedbackUseCase.sendFeedback(email.value, subject.value, message.value)) {
                is Result.Success -> {
                    feedbackProcess.value = FeedbackSendingStatus.Sent
                    email.value = ""
                    subject.value = ""
                    message.value = ""
                }

                is Result.Error -> feedbackProcess.value = FeedbackSendingStatus.Failure(
                    type = response.exception.type,
                    message = response.exception.errorMessage,
                )
            }
        }
    }
}

sealed class FeedbackSendingStatus {
    object Idle : FeedbackSendingStatus()
    object Sending : FeedbackSendingStatus()
    object Sent : FeedbackSendingStatus()
    data class Failure(val type: ExceptionType, val message: Int) : FeedbackSendingStatus()
}