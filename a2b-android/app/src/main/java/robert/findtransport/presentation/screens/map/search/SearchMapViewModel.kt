package robert.findtransport.presentation.screens.map.search

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import robert.findtransport.data.model.MultiRoute
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.presentation.screens.map.MapViewModel
import javax.inject.Inject

@HiltViewModel
class SearchMapViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val transportUseCase: TransportUseCase,
  locationUseCase: LocationUseCase,
  permissionUseCase: PermissionUseCase,
) : MapViewModel(localeUseCase, permissionUseCase, locationUseCase) {

  private val _searchMultiTransports = MutableSharedFlow<Triple<List<MultiRoute>, Stop, Stop>>()
  val searchMultiTransports: Flow<Triple<List<MultiRoute>, Stop, Stop>> get() = _searchMultiTransports

//  private val _searchEmpty = MutableSharedFlow<Unit>()
//  val searchEmpty: Flow<Unit> get() = _searchEmpty

  private val _routeSuccess = MutableSharedFlow<Pair<RouteResult?, RouteResult?>>()
  val transportRouteSuccess: Flow<Pair<RouteResult?, RouteResult?>> get() = _routeSuccess

  fun getMultiRoute(fromId: Int, toId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
//      val from = stopsUseCase.getStop(fromId)
//      val to = stopsUseCase.getStop(toId)

//      transportUseCase.search(from, to, SearchOpenInitiator.HOME.name).let { search ->
//        when (search) {
//          is Result.Success -> when (search.data) {
//            is SearchResult.Multi -> {
//              val multiRoute = search.data.result.map { multiRoute ->
//                val stop = multiRoute.stop
//                val coordinates = stop?.let { multiRouteStop -> stopsUseCase.getStopCoordinates(multiRouteStop) } ?: emptyList()
//
//                multiRoute.copy(stop = multiRoute.stop?.copy(coordinates = coordinates))
//              }
//
//              _searchMultiTransports.emit(Triple(multiRoute, from, to)).also { _loading.emit(false) }
//            }
//            else -> Unit
//          }
//          is Result.Error -> if (search.exception.type == ExceptionType.NO_DATA) {
//            _searchEmpty.emit(Unit)
//            _loading.emit(false)
//          }
//        }
//      }
    }
  }

  fun getRouteSuccess(id: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val route = async {
//        transportUseCase.getTransportRoute(id, reverse = false, isUnderground = false)
//          .stateIn(scope = viewModelScope).value
//          .let { routeResult ->
//            if (!coroutineContext.isActive) return@let null
//            when (routeResult) {
//              is Result.Success -> routeResult.data
//              else -> return@let null
//            }
//          }
      }
      val reverse = async {
//        transportUseCase.getTransportRoute(id, reverse = true, isUnderground = false)
//          .stateIn(scope = viewModelScope).value
//          .let { routeResult ->
//            if (!coroutineContext.isActive) return@let null
//            when (routeResult) {
//              is Result.Success -> routeResult.data
//              else -> return@let null
//            }
//          }
      }

//      _routeSuccess.emit(route.await() to reverse.await())
    }
  }
}
