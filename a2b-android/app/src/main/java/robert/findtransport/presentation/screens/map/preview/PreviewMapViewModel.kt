package robert.findtransport.presentation.screens.map.preview

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import robert.findtransport.data.model.RouteResult
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

  private val _route = MutableSharedFlow<RouteResult>()
  val route: Flow<RouteResult> get() = _route

  fun getTransportRoute(id: Int, underground: Boolean) {
    viewModelScope.launch {
      if (!coroutineContext.isActive) return@launch
      try {
        transportUseCase.getTransportRoute(id, false, underground).collect { routeResult ->
          if (!coroutineContext.isActive) return@collect
          _route.emit(routeResult)
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun getReversedTransportRoute(id: Int, underground: Boolean) {
    viewModelScope.launch {
      if (!coroutineContext.isActive) return@launch
      try {
        transportUseCase.getTransportRoute(id, true, underground).collect { routeResult ->
          if (!coroutineContext.isActive) return@collect
          _route.emit(routeResult)
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }
}
