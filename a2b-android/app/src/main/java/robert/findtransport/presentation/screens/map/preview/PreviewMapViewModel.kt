package robert.findtransport.presentation.screens.map.preview

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.presentation.screens.map.MapViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewMapViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  permissionUseCase: PermissionUseCase,
  locationUseCase: LocationUseCase,
  private val transportUseCase: TransportUseCase,
) : MapViewModel(localeUseCase, permissionUseCase, locationUseCase) {

  private val _routeSuccess = MutableSharedFlow<RouteResult>()
  val routeSuccess: Flow<RouteResult> get() = _routeSuccess

  private val _routeError = MutableSharedFlow<Int>()
  val routeError: Flow<Int> get() = _routeError

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
}
