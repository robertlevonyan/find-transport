package robert.findtransport.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU

open class MapViewModel(
    private val stopsUseCase: StopsUseCase,
    private val localeUseCase: LocaleUseCase,
    private val transportUseCase: TransportUseCase
) : BaseViewModel() {
  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private val _onCurrentLocation = MutableLiveData<Unit>()
  val onCurrentLocation: LiveData<Unit> get() = _onCurrentLocation

  private val _allStops = MutableLiveData<List<SymbolOptions>>()
  val allStops: LiveData<List<SymbolOptions>> get() = _allStops

  private val _metroStops = MutableLiveData<List<SymbolOptions>>()
  val metroStops: LiveData<List<SymbolOptions>> get() = _metroStops

  private val _routeSuccess = MutableLiveData<RouteResult>()
  val routeSuccess: LiveData<RouteResult> get() = _routeSuccess

  private val _routeError = MutableLiveData<Int>()
  val routeError: LiveData<Int> get() = _routeError

  init {
    _locale.postValue(localeUseCase.getCurrentLanguage())
  }

  fun getStops() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        if (!coroutineContext.isActive) return@launch
        val stops = stopsUseCase.getStopsLocations()
        _allStops.postValue(stops)
        val metroStops = stopsUseCase.getMetroStopsLocations()
        _metroStops.postValue(metroStops)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun getTransportRoute(id: Int, underground: Boolean) {
    viewModelScope.launch(Dispatchers.IO) {
      if (!coroutineContext.isActive) return@launch
      try {
        transportUseCase.getTransportRoute(id, false, underground).collect { routeResult ->
          if (!coroutineContext.isActive) return@collect
          when (routeResult) {
            is Result.Success -> {
              val successData = routeResult.data
              _routeSuccess.postValue(successData)
            }
            is Result.Error -> when (routeResult.exception.type) {
              ExceptionType.NAVIGATION_EMPTY, ExceptionType.NAVIGATION_ERROR -> _routeError.postValue(routeResult.exception.errorMessage)
              else -> return@collect
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun getTransportRouteReverse(id: Int, underground: Boolean) {
    viewModelScope.launch(Dispatchers.IO) {
      if (!coroutineContext.isActive) return@launch
      try {
        transportUseCase.getTransportRoute(id, true, underground).collect { routeResult ->
          if (!coroutineContext.isActive) return@collect
          when (routeResult) {
            is Result.Success -> {
              val successData = routeResult.data
              _routeSuccess.postValue(successData)
            }
            is Result.Error -> when (routeResult.exception.type) {
              ExceptionType.NAVIGATION_EMPTY -> _routeError.postValue(routeResult.exception.errorMessage)
              ExceptionType.NAVIGATION_ERROR -> _routeError.postValue(routeResult.exception.errorMessage)
              else -> return@collect
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun getCurrentLocation() {
    _onCurrentLocation.postValue(Unit)
  }

  fun getStopName(stop: Stop): String = when (localeUseCase.getCurrentLanguage()) {
    LNG_EN -> stop.nameEn
    LNG_RU -> stop.nameRu
    else -> stop.nameAm
  }
}
