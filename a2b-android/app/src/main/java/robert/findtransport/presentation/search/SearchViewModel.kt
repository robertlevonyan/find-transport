package robert.findtransport.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.*
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.domain.usecase.history.HistoryUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.presentation.component.ld.SingleLiveEvent
import java.util.*

class SearchViewModel(
  localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val historyUseCase: HistoryUseCase
) : BaseViewModel() {

  private val _loading = MutableLiveData<Boolean>()
  val loading: LiveData<Boolean> get() = _loading

  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private val _fromStop = MutableLiveData<Stop>()
  val fromStop: LiveData<Stop> get() = _fromStop

  private val _toStop = MutableLiveData<Stop>()
  val toStop: LiveData<Stop> get() = _toStop

  private val _searchTransports = SingleLiveEvent<List<Transport>>()
  val searchTransports: LiveData<List<Transport>> get() = _searchTransports

  private val _searchMultiTransports = MutableLiveData<List<MultiRoute>>()
  val searchMultiTransports: LiveData<List<MultiRoute>> get() = _searchMultiTransports

  private val _searchEmpty = MutableLiveData<Unit>()
  val searchEmpty: LiveData<Unit> get() = _searchEmpty

  private val _selectedTransport = MutableLiveData<Transport>()
  val selectedTransport: LiveData<Transport> get() = _selectedTransport

  private val _emptyStop = MutableLiveData<Unit>()
  val emptyStop: LiveData<Unit> get() = _emptyStop

  init {
    _locale.postValue(localeUseCase.getCurrentLanguage())
  }

  fun getData(fromId: Int, toId: Int, addToHistory: Boolean) {
    _loading.postValue(true)
    viewModelScope.launch(Dispatchers.IO) {
      val from = stopsUseCase.getStop(fromId)
      val to = stopsUseCase.getStop(toId)
      if (from == Stop.EMPTY || to == Stop.EMPTY) {
        _emptyStop.postValue(Unit)
        return@launch
      }
      _fromStop.postValue(from)
      _toStop.postValue(to)
      when (val search = transportUseCase.search(from, to)) {
        is Result.Success -> when (search.data) {
          is SearchResult.Single -> {
            _searchTransports.postValue(search.data.result).also { _loading.postValue(false) }
          }
          is SearchResult.Multi -> {
            _searchMultiTransports.postValue(search.data.result).also { _loading.postValue(false) }
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
          _searchEmpty.postValue(Unit)
          _loading.postValue(false)
        }
      }
    }
  }

  fun openTransport(transport: Transport?) {
    _selectedTransport.postValue(transport ?: return)
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
    }
  }
}
