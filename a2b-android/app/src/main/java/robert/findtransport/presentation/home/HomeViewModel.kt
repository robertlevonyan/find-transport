package robert.findtransport.presentation.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.enums.NearbyStopStatus
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.extensions.asPair
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val permissionUseCase: PermissionUseCase
) : BaseViewModel() {
  private val _allTransportsError = MutableSharedFlow<Unit>()
  val allTransportsError: Flow<Unit> get() = _allTransportsError

  private val _fromStop = MutableStateFlow(Stop.EMPTY)
  val fromStop: StateFlow<Stop> get() = _fromStop

  private val _fromError = MutableSharedFlow<Int>()
  val fromError: Flow<Int> get() = _fromError

//  private val _toStop = MutableStateFlow(Stop.EMPTY)
//  val toStop: StateFlow<Stop> get() = _toStop

  private val _toError = MutableSharedFlow<Int>()
  val toError: Flow<Int> get() = _toError

//  private val _hasLocationPermission = MutableStateFlow(LocationPermission.NO_PERMISSION)
//  val hasLocationPermission: Flow<LocationPermission> get() = _hasLocationPermission

  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: Flow<String> get() = _locale

  private val _openSearch = MutableSharedFlow<Pair<Int, Int>>()
  val openSearch: Flow<Pair<Int, Int>> get() = _openSearch

  init {
//    val hasPermission = permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//    startFindNearbyLocation(hasPermission)
  }

//  fun startFindNearbyLocation(hasPermission: Boolean)  {
//    viewModelScope.launch {
//      _hasLocationPermission.emit(
//        if (hasPermission) {
//          NearbyStopStatus.LOADING
//        } else {
//          NearbyStopStatus.NO_PERMISSION
//        }
//      )
//      findNearbyLocation(hasPermission, stopsUseCase.getStops())
//    }
//  }

//  private suspend fun findNearbyLocation(
//    hasPermission: Boolean,
//    stops: List<Stop>,
//  ) = withContext(Dispatchers.IO) {
//    if (hasPermission) {
//      stopsUseCase.getNearbyStop(stops, this).collect { nearbyStop ->
//        if (nearbyStop == Stop.EMPTY) {
//          withContext(Dispatchers.Main) {
//            _hasLocationPermission.emit(NearbyStopStatus.HAS_PERMISSION)
//          }
//          return@collect
//        }
//        _fromStop.value = nearbyStop
//        _hasLocationPermission.emit(NearbyStopStatus.HAS_PERMISSION)
//      }
//    }
//  }

  suspend fun getCoordinates(stop: Stop): Pair<Double, Double>? =
    stopsUseCase.getStopCoordinates(stop).firstOrNull()?.asPair()
}
