package robert.findtransport.presentation.detail

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
  private val localeUseCase: LocaleUseCase,
  private val transportUseCase: TransportUseCase,
) : BaseViewModel() {

  private val _selectedTransport = MutableStateFlow(Transport.EMPTY)
  val selectedTransport: StateFlow<Transport> get() = _selectedTransport

  private val _openPassingTransports = MutableSharedFlow<Stop>()
  val openPassingTransports: Flow<Stop> get() = _openPassingTransports

  private val _hasOptions = MutableStateFlow(false)
  val hasOptions: StateFlow<Boolean> get() = _hasOptions

  private val _showPrimary = MutableStateFlow(true)
  val showPrimary: StateFlow<Boolean> get() = _showPrimary

  private val _fromStop = MutableStateFlow(Stop.EMPTY)
  val fromStop: Flow<Stop> get() = _fromStop

  private val _toStop = MutableStateFlow(Stop.EMPTY)
  val toStop: Flow<Stop> get() = _toStop

  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: StateFlow<String> get() = _locale

  fun getTransport(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.getTransportById(id).collect { transport ->
        _selectedTransport.value = transport
      }
    }
  }

  fun setHasOptions(has: Boolean) {
    _hasOptions.value = has
  }

  fun togglePrimary(primary: Boolean) {
    _showPrimary.value = primary
  }

  fun setFromStop(stop: Stop) {
    _fromStop.value = stop
  }

  fun setToStop(stop: Stop) {
    _toStop.value = stop
  }

  fun getStopName(stop: Stop): String =
    when (localeUseCase.getCurrentLanguage()) {
      LNG_EN -> stop.nameEn
      LNG_RU -> stop.nameRu
      else -> stop.nameAm
    }

  fun onShowTransportsClicked(stop: Stop) {
    viewModelScope.launch {
      _openPassingTransports.emit(stop)
    }
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
      toggleFinishAction.invoke()
    }
  }
}
