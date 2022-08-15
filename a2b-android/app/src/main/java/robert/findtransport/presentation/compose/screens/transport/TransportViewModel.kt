package robert.findtransport.presentation.compose.screens.transport

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
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

  private val _selectedTransport = MutableStateFlow(Transport.EMPTY)
  val selectedTransport: StateFlow<Transport> get() = _selectedTransport

  private val _fromStop = MutableStateFlow(Stop.EMPTY)
  val fromStop: Flow<Stop> get() = _fromStop

  private val _toStop = MutableStateFlow(Stop.EMPTY)
  val toStop: Flow<Stop> get() = _toStop

  fun getTransport(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.getTransportById(id).collect { transport ->
        _selectedTransport.value = transport
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
