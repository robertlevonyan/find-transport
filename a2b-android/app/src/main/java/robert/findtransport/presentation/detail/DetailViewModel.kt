package robert.findtransport.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU

class DetailViewModel(
    private val localeUseCase: LocaleUseCase,
    private val transportUseCase: TransportUseCase,
) : BaseViewModel() {

  private val _selectedTransport = MutableLiveData<Transport>()
  val selectedTransport: LiveData<Transport> get() = _selectedTransport

  private val _openPassingTransports = MutableLiveData<Stop>()
  val openPassingTransports: LiveData<Stop> get() = _openPassingTransports

  private val _hasOptions = MutableLiveData<Boolean>()
  val hasOptions: LiveData<Boolean> get() = _hasOptions

  private val _showPrimary = MutableLiveData<Boolean>().apply { value = true }
  val showPrimary: LiveData<Boolean> get() = _showPrimary

  private val _fromStop = MutableLiveData<Stop>()
  val fromStop: LiveData<Stop> get() = _fromStop

  private val _toStop = MutableLiveData<Stop>()
  val toStop: LiveData<Stop> get() = _toStop

  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private val _openMap = MutableLiveData<Unit>()
  val openMap: LiveData<Unit> get() = _openMap

  init {
    _locale.postValue(localeUseCase.getCurrentLanguage())
  }

  fun getTransport(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.getTransportById(id).collect { transport ->
        _selectedTransport.postValue(transport)
        println(transport.stopsReversed.map { it.id })
      }
    }
  }

  fun setHasOptions(has: Boolean) {
    _hasOptions.postValue(has)
  }

  fun togglePrimary(primary: Boolean) {
    _showPrimary.postValue(primary)
  }

  fun setFromStop(stop: Stop) {
    _fromStop.postValue(stop)
  }

  fun setToStop(stop: Stop) {
    _toStop.postValue(stop)
  }

  fun getStopName(stop: Stop): String =
      when (localeUseCase.getCurrentLanguage()) {
        LNG_EN -> stop.nameEn
        LNG_RU -> stop.nameRu
        else -> stop.nameAm
      }

  fun openMapClick() {
    _openMap.postValue(Unit)
  }

  fun onShowTransportsClicked(stop: Stop) {
    _openPassingTransports.postValue(stop)
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
      toggleFinishAction.invoke()
    }
  }
}
