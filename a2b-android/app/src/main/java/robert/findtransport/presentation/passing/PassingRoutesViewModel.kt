package robert.findtransport.presentation.passing

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import javax.inject.Inject

@HiltViewModel
class PassingRoutesViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase
) : BaseViewModel() {
  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: StateFlow<String> get() = _locale

  private val _stopReceived = MutableSharedFlow<Stop>()
  val stopReceived: Flow<Stop> get() = _stopReceived

  private val _stopTransports = MutableSharedFlow<List<Transport>>()
  val stopTransports: Flow<List<Transport>> get() = _stopTransports

  fun getStop(stopId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val stop = stopsUseCase.getStop(stopId)
      _stopReceived.emit(stop)
    }
  }

  fun getTransports(stopId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val transports = transportUseCase.getTransportsForStop(stopId)
      _stopTransports.emit(transports)
    }
  }
}