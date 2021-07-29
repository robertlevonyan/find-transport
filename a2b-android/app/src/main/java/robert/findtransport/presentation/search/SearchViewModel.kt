package robert.findtransport.presentation.search

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.*
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.usecase.history.HistoryUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import java.util.*

class SearchViewModel(
  localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val historyUseCase: HistoryUseCase
) : BaseViewModel() {
  private val _loading = MutableSharedFlow<Boolean>()
  val loading: Flow<Boolean> get() = _loading

  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: StateFlow<String> get() = _locale

  private val _fromStop = MutableSharedFlow<Stop>()
  val fromStop: Flow<Stop> get() = _fromStop

  private val _toStop = MutableSharedFlow<Stop>()
  val toStop: Flow<Stop> get() = _toStop

  private val _searchTransports = MutableStateFlow<List<Transport>>(emptyList())
  val searchTransports: Flow<List<Transport>> get() = _searchTransports

  private val _searchMultiTransports = MutableStateFlow<List<MultiRoute>>(emptyList())
  val searchMultiTransports: Flow<List<MultiRoute>> get() = _searchMultiTransports

  private val _searchEmpty = MutableSharedFlow<Unit>()
  val searchEmpty: Flow<Unit> get() = _searchEmpty

  private val _selectedTransport = MutableSharedFlow<Transport>()
  val selectedTransport: Flow<Transport> get() = _selectedTransport

  private val _emptyStop = MutableSharedFlow<Unit>()
  val emptyStop: Flow<Unit> get() = _emptyStop

  fun getData(fromId: Int, toId: Int, addToHistory: Boolean) {
    viewModelScope.launch(Dispatchers.IO) {
      _loading.emit(true)
      val from = stopsUseCase.getStop(fromId)
      val to = stopsUseCase.getStop(toId)

      if (from == Stop.EMPTY || to == Stop.EMPTY) {
        _emptyStop.emit(Unit)
        return@launch
      }
      _fromStop.emit(from)
      _toStop.emit(to)

      when (val search = transportUseCase.search(from, to)) {
        is Result.Success -> when (search.data) {
          is SearchResult.Single -> {
            delay(100)
            _searchTransports.value = search.data.result
            _loading.emit(false)
          }
          is SearchResult.Multi -> {
            delay(100)
            _searchMultiTransports.value = search.data.result
            _loading.emit(false)
          }
        }.run {
          if (addToHistory) {
            historyUseCase.saveInHistory(
              History(
                fromStop = from,
                toStop = to,
                timestamp = Date().time
              )
            )
          }
        }
        is Result.Error -> if (search.exception.type == ExceptionType.NO_DATA) {
          _searchEmpty.emit(Unit)
          _loading.emit(false)
        }
      }
    }
  }

  fun openTransport(transport: Transport?) {
    viewModelScope.launch {
      _selectedTransport.emit(transport ?: return@launch)
    }
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
    }
  }
}
