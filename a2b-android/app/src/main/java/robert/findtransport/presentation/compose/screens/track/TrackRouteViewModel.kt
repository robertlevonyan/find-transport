package robert.findtransport.presentation.compose.screens.track

import android.location.Location
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class TrackRouteViewModel @Inject constructor(
  private val transportUseCase: TransportUseCase,
  private val stopsUseCase: StopsUseCase,
  private val locationUseCase: LocationUseCase,
  localeUseCase: LocaleUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()

  private val _selectedTransport = MutableSharedFlow<Transport>()
  val selectedTransport: Flow<Transport> get() = _selectedTransport

  private val _currentStop = MutableStateFlow(Stop.EMPTY)
  val currentStop: StateFlow<Stop> get() = _currentStop

  private val _notifyNextStop = MutableStateFlow(Stop.EMPTY)
  val notifyNextStop: StateFlow<Stop> get() = _notifyNextStop

  private val _notifyArrived = MutableStateFlow(false)
  val notifyArrived: StateFlow<Boolean> get() = _notifyArrived

  private val _notifyStop = MutableSharedFlow<Unit>().apply {
    onStart { notifyStopObservers.incrementAndGet() }
    onCompletion { notifyStopObservers.decrementAndGet() }
  }
  private val notifyStopObservers = AtomicInteger(0)

  init {
    if (notifyStopObservers.get() > 0) {
      viewModelScope.launch { _notifyStop.emit(Unit) }
    }
  }

  private suspend fun subscribeToLocationChanges() =
    locationUseCase.subscribeToLocationUpdates().stateIn(scope = viewModelScope)

  private suspend fun getNearbyStopNames(
    location: Location,
    transport: Transport,
    start: Stop,
    destination: Stop,
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.getNearbyStopFromTransport(
        transport = transport,
        start = start,
        destination = destination,
        location = location,
        coroutineScope = this
      ).collect { stops ->
        val current = stops.first
        val predestination = stops.second

        if (current == Stop.EMPTY) return@collect

        val currentStopValue = _currentStop.value

//        if (currentStopValue != Stop.EMPTY) {
//          _previousStop.emit(currentStopValue)
//        }
        _currentStop.emit(current)
//        _predestination.emit(predestination)

        if (current.id == predestination.id) {
          _notifyNextStop.emit(predestination)
        }
        if (current.id == destination.id && !_notifyArrived.value) {
          _notifyArrived.value = true
        }
      }
    }
  }

  fun initData(transportId: Int, fromId: Int, toId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val selectedTransport = async { transportUseCase.getTransportById(transportId) }
      val fromStop = async { stopsUseCase.getStop(fromId) }
      val toStop = async { stopsUseCase.getStop(toId) }

//      _fromStop.emit(fromStop.await())
//      _toStop.emit(toStop.await())
      launch { selectedTransport.await().collect(_selectedTransport::emit) }

      subscribeToLocationChanges()
        .combine(selectedTransport.await()) { location: Location, transport: Transport ->
          location to transport
        }
        .collect { locationAndTransport ->
          getNearbyStopNames(
            location = locationAndTransport.first,
            transport = locationAndTransport.second,
            start = fromStop.await(),
            destination = toStop.await(),
          )
        }
    }
  }
}
