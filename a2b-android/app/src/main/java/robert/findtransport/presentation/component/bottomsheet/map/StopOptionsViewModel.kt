package robert.findtransport.presentation.component.bottomsheet.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import javax.inject.Inject

@HiltViewModel
class StopOptionsViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
) : BaseViewModel() {
  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private val _currentStop = MutableLiveData<Stop>()
  val currentStop: LiveData<Stop> get() = _currentStop

  private val _emptyStop = MutableLiveData<Unit>()
  val emptyStop: LiveData<Unit> get() = _emptyStop

  private val _fromStop = MutableLiveData<Stop>()
  val fromStop: LiveData<Stop> get() = _fromStop

  private val _toStop = MutableLiveData<Stop>()
  val toStop: LiveData<Stop> get() = _toStop

  private val _passingTransports = MutableLiveData<Stop>()
  val passingTransports: LiveData<Stop> get() = _passingTransports

  init {
    _locale.postValue(localeUseCase.getCurrentLanguage())
  }

  fun getCurrentStop(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val stop = stopsUseCase.getStop(id)
      if (stop == Stop.EMPTY) {
        _emptyStop.postValue(Unit)
        return@launch
      }
      _currentStop.postValue(stop)
    }
  }

  fun setFrom() {
    _fromStop.postValue(_currentStop.value)
  }

  fun setTo() {
    _toStop.postValue(_currentStop.value)
  }

  fun showTransports() {
    _passingTransports.postValue(_currentStop.value)
  }
}
