package robert.findtransport.presentation.feedback

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.usecase.feedback.FeedbackUseCase

class FeedbackViewModel(private val feedbackUseCase: FeedbackUseCase) : BaseViewModel() {
  private var email = ""
  private var subject = ""
  private var message = ""

  private val _feedbackSent = MutableSharedFlow<Unit>()
  val feedbackSent: Flow<Unit> get() = _feedbackSent
  private val _errorEmail = MutableSharedFlow<Int>()
  val errorEmail: Flow<Int> get() = _errorEmail
  private val _errorSubject = MutableSharedFlow<Int>()
  val errorSubject: Flow<Int> get() = _errorSubject
  private val _errorMessage = MutableSharedFlow<Int>()
  val errorMessage: Flow<Int> get() = _errorMessage
  private val _showHideLoading = MutableSharedFlow<Boolean>()
  val showHideLoading: Flow<Boolean> get() = _showHideLoading

  fun onEmailInput(input: CharSequence?) {
    email = (input ?: "").toString()
  }

  fun onSubjectInput(input: CharSequence?) {
    subject = (input ?: "").toString()
  }

  fun onMessageInput(input: CharSequence?) {
    message = (input ?: "").toString()
  }

  fun sendFeedback() {
    viewModelScope.launch(Dispatchers.IO) {
      _showHideLoading.emit(true)
      when (val response = feedbackUseCase.sendFeedback(email, subject, message)) {
        is Result.Success -> {
          _feedbackSent.emit(Unit)
          _showHideLoading.emit(false)
        }
        is Result.Error -> {
          when (response.exception.type) {
            ExceptionType.ERROR_EMAIL,
            ExceptionType.WRONG_EMAIL,
            ExceptionType.ERROR_SUBJECT,
            ExceptionType.ERROR_MESSAGE,
            ExceptionType.SHORT_MESSAGE -> _errorMessage.emit(response.exception.errorMessage)
            else -> return@launch
          }
          _showHideLoading.emit(false)
        }
      }
    }
  }
}
