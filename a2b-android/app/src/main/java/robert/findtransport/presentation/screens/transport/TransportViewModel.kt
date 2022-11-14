package robert.findtransport.presentation.screens.transport

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import javax.inject.Inject

@HiltViewModel
class TransportViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val transportUseCase: TransportUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val selectedTransport = MutableStateFlow(Transport.EMPTY)

  fun getTransport(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.getTransportById(id).collect { transport ->
        selectedTransport.value = transport
      }
    }
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
      toggleFinishAction.invoke()
    }
  }
}
