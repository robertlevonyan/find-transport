package robert.findtransport.presentation.screens.passingroutes

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    val stop = MutableSharedFlow<Stop>()
    val transports = MutableStateFlow<List<Transport>>(emptyList())

    fun getStopAndTransports(stopId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            launch { stop.emit(stopsUseCase.getStop(stopId)) }
            launch { transports.emit(transportUseCase.getTransportsForStop(stopId)) }
        }
    }
}