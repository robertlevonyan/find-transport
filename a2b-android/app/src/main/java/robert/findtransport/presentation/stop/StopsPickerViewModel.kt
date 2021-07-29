package robert.findtransport.presentation.stop

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase

class StopsPickerViewModel(
  private val stopsUseCase: StopsUseCase,
  private val localeUseCase: LocaleUseCase
) : BaseViewModel() {
  val allStops: Flow<PagingData<Stop>> = stopsUseCase.getStopsPaged().cachedIn(viewModelScope + Dispatchers.IO)

  private val _autocompleteStops = MutableSharedFlow<List<Stop>>()
  val autocompleteStops: Flow<List<Stop>> get() = _autocompleteStops

  private val _selectedStop = MutableSharedFlow<Stop>()
  val selectedStop: Flow<Stop> get() = _selectedStop

  private val _searchMode = MutableStateFlow(false)
  val searchMode: Flow<Boolean> get() = _searchMode

  private val _showNoData = MutableStateFlow(false)
  val showNoData: Flow<Boolean> get() = _showNoData

  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: StateFlow<String> get() = _locale

  fun findStops(word: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val locale = localeUseCase.getCurrentLanguage()
      val stops = stopsUseCase.getStopsAutocomplete(word, locale)
      withContext(Dispatchers.Main) {
        if (stops.isEmpty() && word.isEmpty()) {
          _showNoData.value = false
        } else if (stops.isEmpty() && word.isNotEmpty()) {
          _autocompleteStops.emit(stops)
          _showNoData.value = true
        } else {
          _autocompleteStops.emit(stops)
          _showNoData.value = false
        }
      }
    }
  }

  fun onStopClicked(stop: Stop) {
    viewModelScope.launch {
      _selectedStop.emit(stop)
    }
  }

  fun toggleSearchMode() {
    _searchMode.value = !_searchMode.value
  }
}
