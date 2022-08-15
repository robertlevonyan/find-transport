package robert.findtransport.presentation.compose.screens.passing

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()

  private val stopFlow = MutableSharedFlow<Stop>()
  val stop get() = stopFlow.asSharedFlow()

  private val transportsFlow = MutableStateFlow<List<Transport>>(emptyList())
  val transports get() = transportsFlow.asStateFlow()

  fun getStopAndTransports(stopId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      launch {
        val stop = stopsUseCase.getStop(stopId)
        stopFlow.emit(stop)
      }
      launch {
        val transports = transportUseCase.getTransportsForStop(stopId)
        transportsFlow.emit(transports)
      }
    }
  }
}