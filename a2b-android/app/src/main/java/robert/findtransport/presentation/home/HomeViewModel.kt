package robert.findtransport.presentation.home

import android.Manifest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.enums.LocationPermission
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.rate.RateUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.presentation.component.ld.SingleLiveEvent

class HomeViewModel(
    localeUseCase: LocaleUseCase,
    private val stopsUseCase: StopsUseCase,
    private val transportUseCase: TransportUseCase,
    private val permissionUseCase: PermissionUseCase,
    private val rateUseCase: RateUseCase
) : BaseViewModel() {
  private val _allTransportsError = MutableLiveData<Unit>()
  val allTransportsError: LiveData<Unit> get() = _allTransportsError

  private val _fromStop = MutableLiveData<Stop>()
  val fromStop: LiveData<Stop> get() = _fromStop

  private val _fromError = MutableLiveData<Int>()
  val fromError: LiveData<Int> get() = _fromError

  private val _toStop = MutableLiveData<Stop>()
  val toStop: LiveData<Stop> get() = _toStop

  private val _toError = MutableLiveData<Int>()
  val toError: LiveData<Int> get() = _toError

  private val _hasLocationPermission = MutableLiveData<LocationPermission>()
  val hasLocationPermission: LiveData<LocationPermission> get() = _hasLocationPermission

  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private val _openMap = SingleLiveEvent<Unit>()
  val openMap: LiveData<Unit> get() = _openMap

  private val _openStops = SingleLiveEvent<Int>()
  val openStops: LiveData<Int> get() = _openStops

  private val _openSearch = SingleLiveEvent<Pair<Int, Int>>()
  val openSearch: LiveData<Pair<Int, Int>> get() = _openSearch

  private val _showRate = MutableLiveData<Boolean>()
  val showRate: LiveData<Boolean> get() = _showRate

  private val _openRate = MutableLiveData<Unit>()
  val openRate: LiveData<Unit> get() = _openRate

  private val _openUpdate = MutableLiveData<Unit>()
  val openUpdate: LiveData<Unit> get() = _openUpdate

  private val scope = CoroutineScope(Dispatchers.IO)
  private var job: Job? = null

  init {
    job = scope.launch {
      delay(1000)
    }

    _locale.postValue(localeUseCase.getCurrentLanguage())
    rateUseCase.updateInterval()
    _showRate.postValue(rateUseCase.showDialog())
    viewModelScope.launch {
      if (!transportUseCase.areJoinsCached() || !transportUseCase.areTransportsCached()
          || !stopsUseCase.areLocationsCached() || !stopsUseCase.areStopsCached()) {
        _openUpdate.postValue(Unit)
      }
      val hasPermission = permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      startFindNearbyLocation(hasPermission)
    }
  }

  fun startFindNearbyLocation(hasPermission: Boolean) {
    viewModelScope.launch {
      _hasLocationPermission.postValue(if (hasPermission) LocationPermission.LOADING else LocationPermission.NO_PERMISSION)
      findNearbyLocation(hasPermission, stopsUseCase.getStops())
    }
  }

  private suspend fun findNearbyLocation(hasPermission: Boolean, stops: List<Stop>) = withContext(Dispatchers.IO) {
    if (hasPermission) {
      stopsUseCase.getNearbyStop(stops, this).collect { nearbyStop ->
        if (nearbyStop == Stop.EMPTY) {
          withContext(Dispatchers.Main) {
            _hasLocationPermission.postValue(LocationPermission.HAS_PERMISSION)
          }
          return@collect
        }
        _fromStop.postValue(nearbyStop)
        _hasLocationPermission.postValue(LocationPermission.HAS_PERMISSION)
      }
    }
  }

  fun notifyOpenMap() {
    _openMap.postValue(Unit)
  }

  fun notifyOpenStops(type: Int) {
    _openStops.postValue(type)
  }

  fun setFromStop(stopId: Int) {
    viewModelScope.launch {
      val stop = stopsUseCase.getStop(stopId)
      _fromStop.postValue(stop)
    }
  }

  fun setToStop(stopId: Int) {
    viewModelScope.launch {
      val stop = stopsUseCase.getStop(stopId)
      _toStop.postValue(stop)
    }
  }

  fun swapStops() {
    val from = _fromStop.value ?: Stop.EMPTY
    val to = _toStop.value ?: Stop.EMPTY
    _fromStop.postValue(to)
    _toStop.postValue(from)
  }

  fun openRate() {
    rateUseCase.setRate()
    _openRate.postValue(Unit)
    _showRate.postValue(false)
  }

  fun dismissRate() {
    _showRate.postValue(false)
  }

  fun search() {
    val from = _fromStop.value
    val to = _toStop.value

    viewModelScope.launch(Dispatchers.IO) {
      when (val search = transportUseCase.searchCheck(from, to)) {
        is Result.Success -> _openSearch.postValue((from?.id ?: 0) to (to?.id ?: 0))
        is Result.Error -> when (search.exception.type) {
          ExceptionType.EMPTY_OR_WRONG_FROM -> _fromError.postValue(search.exception.errorMessage)
          ExceptionType.EMPTY_OR_WRONG_TO -> _toError.postValue(search.exception.errorMessage)
          ExceptionType.SAME_STOPS -> _toError.postValue(search.exception.errorMessage)
          else -> return@launch
        }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    stopsUseCase.disconnectFromLocationService()
  }
}
