package robert.findtransport.presentation.passing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase

class PassingRoutesViewModel(
    localeUseCase: LocaleUseCase,
    private val stopsUseCase: StopsUseCase,
    private val transportUseCase: TransportUseCase
) : BaseViewModel() {

  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private val _stopReceived = MutableLiveData<Stop>()
  val stopReceived: LiveData<Stop> get() = _stopReceived

  private val _stopTransports = MutableLiveData<List<Transport>>()
  val stopTransports: LiveData<List<Transport>> get() = _stopTransports

  init {
    _locale.postValue(localeUseCase.getCurrentLanguage())
  }

  fun getStop(stopId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val stop = stopsUseCase.getStop(stopId)
      _stopReceived.postValue(stop)
    }
  }

  fun getTransports(stopId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val transports = transportUseCase.getTransportsForStop(stopId)
      _stopTransports.postValue(transports)
    }
  }
}