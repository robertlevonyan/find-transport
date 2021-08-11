package robert.findtransport.presentation.map

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU

open class MapViewModel(
  private val stopsUseCase: StopsUseCase,
  private val localeUseCase: LocaleUseCase,
  private val transportUseCase: TransportUseCase,
  private val locationUseCase: LocationUseCase,
) : BaseViewModel() {
  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: Flow<String> get() = _locale

  private val _allStops = MutableStateFlow<List<PointAnnotationOptions>>(emptyList())
  val allStops: Flow<List<PointAnnotationOptions>> get() = _allStops

  private val _metroStops = MutableStateFlow<List<PointAnnotationOptions>>(emptyList())
  val metroStops: Flow<List<PointAnnotationOptions>> get() = _metroStops

  private val _routeSuccess = MutableSharedFlow<RouteResult>()
  val routeSuccess: Flow<RouteResult> get() = _routeSuccess

  private val _routeError = MutableSharedFlow<Int>()
  val routeError: Flow<Int> get() = _routeError

  private val _currentLocation = MutableStateFlow(
    Location("").apply {
      latitude = DEFAULT_LATITUDE
      longitude = DEFAULT_LONGITUDE
    }
  )
  val currentLocation: StateFlow<Location> get() = _currentLocation

  init {
    viewModelScope.launch {
      locationUseCase.subscribeToCurrentLocation().collect {
        _currentLocation.value = it
      }
    }
  }

  fun getStops() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        if (!coroutineContext.isActive) return@launch
        val stops = stopsUseCase.getStopsLocations()
        _allStops.value = stops
        val metroStops = stopsUseCase.getMetroStopsLocations()
        _metroStops.value = metroStops
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
              _routeSuccess.emit(successData)
            }
            is Result.Error -> when (routeResult.exception.type) {
              ExceptionType.NAVIGATION_EMPTY, ExceptionType.NAVIGATION_ERROR -> _routeError.emit(routeResult.exception.errorMessage)
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
              _routeSuccess.emit(routeResult.data)
            }
            is Result.Error -> when (routeResult.exception.type) {
              ExceptionType.NAVIGATION_EMPTY -> _routeError.emit(routeResult.exception.errorMessage)
              ExceptionType.NAVIGATION_ERROR -> _routeError.emit(routeResult.exception.errorMessage)
              else -> return@collect
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun getStopName(stop: Stop): String = when (localeUseCase.getCurrentLanguage()) {
    LNG_EN -> stop.nameEn
    LNG_RU -> stop.nameRu
    else -> stop.nameAm
  }
}
