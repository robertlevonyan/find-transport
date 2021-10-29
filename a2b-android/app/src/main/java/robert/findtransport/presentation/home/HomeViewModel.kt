package robert.findtransport.presentation.home

import android.Manifest
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val permissionUseCase: PermissionUseCase,
  private val rateUseCase: RateUseCase
) : BaseViewModel() {
  private val _allTransportsError = MutableSharedFlow<Unit>()
  val allTransportsError: Flow<Unit> get() = _allTransportsError

  private val _fromStop = MutableStateFlow(Stop.EMPTY)
  val fromStop: Flow<Stop> get() = _fromStop

  private val _fromError = MutableSharedFlow<Int>()
  val fromError: Flow<Int> get() = _fromError

  private val _toStop = MutableStateFlow(Stop.EMPTY)
  val toStop: Flow<Stop> get() = _toStop

  private val _toError = MutableSharedFlow<Int>()
  val toError: Flow<Int> get() = _toError

  private val _hasLocationPermission = MutableStateFlow(LocationPermission.NO_PERMISSION)
  val hasLocationPermission: Flow<LocationPermission> get() = _hasLocationPermission

  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: Flow<String> get() = _locale

  private val _openMap = MutableSharedFlow<Unit>()
  val openMap: Flow<Unit> get() = _openMap

  private val _openStops = MutableSharedFlow<Int>()
  val openStops: Flow<Int> get() = _openStops

  private val _openSearch = MutableSharedFlow<Pair<Int, Int>>()
  val openSearch: Flow<Pair<Int, Int>> get() = _openSearch

  private val _showRate = MutableStateFlow(rateUseCase.showDialog())
  val showRate: Flow<Boolean> get() = _showRate

  private val _openRate = MutableSharedFlow<Unit>()
  val openRate: Flow<Unit> get() = _openRate

  private val _openUpdate = MutableSharedFlow<Unit>()
  val openUpdate: Flow<Unit> get() = _openUpdate

  private val scope = CoroutineScope(Dispatchers.IO)
  private var job: Job? = null

  init {
    job = scope.launch {
      delay(1000)
    }

    rateUseCase.updateInterval()
    viewModelScope.launch {
      if (!transportUseCase.areJoinsCached() || !transportUseCase.areTransportsCached()
        || !stopsUseCase.areLocationsCached() || !stopsUseCase.areStopsCached()
      ) {
        _openUpdate.emit(Unit)
      }
      val hasPermission = permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      startFindNearbyLocation(hasPermission)
    }
  }

  fun startFindNearbyLocation(hasPermission: Boolean) {
    viewModelScope.launch {
      _hasLocationPermission.emit(if (hasPermission) LocationPermission.LOADING else LocationPermission.NO_PERMISSION)
      findNearbyLocation(hasPermission, stopsUseCase.getStops())
    }
  }

  private suspend fun findNearbyLocation(hasPermission: Boolean, stops: List<Stop>) = withContext(Dispatchers.IO) {
    if (hasPermission) {
      stopsUseCase.getNearbyStop(stops, this).collect { nearbyStop ->
        if (nearbyStop == Stop.EMPTY) {
          withContext(Dispatchers.Main) {
            _hasLocationPermission.emit(LocationPermission.HAS_PERMISSION)
          }
          return@collect
        }
        _fromStop.emit(nearbyStop)
        _hasLocationPermission.emit(LocationPermission.HAS_PERMISSION)
      }
    }
  }

  fun notifyOpenMap() {
    viewModelScope.launch {
      _openMap.emit(Unit)
    }
  }

  fun notifyOpenStops(type: Int) {
    viewModelScope.launch {
      _openStops.emit(type)
    }
  }

  fun setFromStop(stopId: Int) {
    viewModelScope.launch {
      val stop = stopsUseCase.getStop(stopId)
      _fromStop.emit(stop)
    }
  }

  fun setToStop(stopId: Int) {
    viewModelScope.launch {
      val stop = stopsUseCase.getStop(stopId)
      _toStop.emit(stop)
    }
  }

  fun swapStops() {
    val from = _fromStop.value
    val to = _toStop.value

    viewModelScope.launch {
      _fromStop.emit(to)
      _toStop.emit(from)
    }
  }

  fun openRate() {
    viewModelScope.launch {
      rateUseCase.setRate()
      _openRate.emit(Unit)
      _showRate.emit(false)
    }
  }

  fun dismissRate() {
    viewModelScope.launch {
      _showRate.emit(false)
    }
  }

  fun search() {
    val from = _fromStop.value
    val to = _toStop.value

    viewModelScope.launch(Dispatchers.IO) {
      when (val search = transportUseCase.searchCheck(from, to)) {
        is Result.Success -> _openSearch.emit(from.id to to.id)
        is Result.Error -> when (search.exception.type) {
          ExceptionType.EMPTY_OR_WRONG_FROM -> _fromError.emit(search.exception.errorMessage)
          ExceptionType.EMPTY_OR_WRONG_TO -> _toError.emit(search.exception.errorMessage)
          ExceptionType.SAME_STOPS -> _toError.emit(search.exception.errorMessage)
          else -> return@launch
        }
      }
    }
  }
}
