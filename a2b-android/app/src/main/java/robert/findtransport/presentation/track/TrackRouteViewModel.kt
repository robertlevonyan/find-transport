package robert.findtransport.presentation.track

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
  private val localeUseCase: LocaleUseCase,
) : BaseViewModel() {
  private val _selectedTransport = MutableSharedFlow<Transport>()
  val selectedTransport: Flow<Transport> get() = _selectedTransport

  private val _fromStop = MutableSharedFlow<Stop>()
  val fromStop: Flow<Stop> get() = _fromStop

  private val _toStop = MutableSharedFlow<Stop>()
  val toStop: Flow<Stop> get() = _toStop

  private val _currentStop = MutableStateFlow(Stop.EMPTY)
  val currentStop: Flow<Stop> get() = _currentStop

  private val _previousStop = MutableStateFlow(Stop.EMPTY)
  val previousStop: Flow<Stop> get() = _previousStop

  private val _predestination = MutableSharedFlow<Stop>()
  val predestination: Flow<Stop> get() = _predestination

  private val _notifyNextStop = MutableStateFlow(Stop.EMPTY)
  val notifyNextStop: Flow<Stop> get() = _notifyNextStop

  private val _notifyArrived = MutableSharedFlow<Unit?>()
  val notifyArrived: Flow<Unit?> get() = _notifyArrived

  private val _notifyStop = MutableSharedFlow<Unit>().apply {
    onStart { notifyStopObservers.incrementAndGet() }
    onCompletion { notifyStopObservers.decrementAndGet() }
  }
  private val notifyStopObservers = AtomicInteger(0)

  val currentLanguage: String
    get() = localeUseCase.getCurrentLanguage()

  init {
    if (notifyStopObservers.get() > 0) {
      viewModelScope.launch { _notifyStop.emit(Unit) }
    }
  }

  private suspend fun subscribeToLocationChanges() =
    locationUseCase.subscribeToLocationUpdates().stateIn(viewModelScope)

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

        if (currentStopValue != Stop.EMPTY) {
          _previousStop.emit(currentStopValue)
        }
        _currentStop.emit(current)
        _predestination.emit(predestination)

        if (current.id == predestination.id) {
          _notifyNextStop.emit(predestination)
        }
        if (current.id == destination.id && _notifyArrived.firstOrNull() == null) {
          _notifyArrived.emit(Unit)
        }
      }
    }
  }

  fun initData(transportId: Int, fromId: Int, toId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val selectedTransport = async { transportUseCase.getTransportById(transportId) }
      val fromStop = async { stopsUseCase.getStop(fromId) }
      val toStop = async { stopsUseCase.getStop(toId) }

      _fromStop.emit(fromStop.await())
      _toStop.emit(toStop.await())
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
