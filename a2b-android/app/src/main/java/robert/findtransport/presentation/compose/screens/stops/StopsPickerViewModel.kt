package robert.findtransport.presentation.compose.screens.stops

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import javax.inject.Inject

@HiltViewModel
class StopsPickerViewModel @Inject constructor(
  private val stopsUseCase: StopsUseCase,
  localeUseCase: LocaleUseCase,
) : BaseViewModel() {
  val allStops: Flow<PagingData<Stop>> = stopsUseCase.getStopsPaged()
    .cachedIn(viewModelScope + Dispatchers.IO)

  private val _autocompleteStops = MutableSharedFlow<List<Stop>>()
  val autocompleteStops get() = _autocompleteStops.asSharedFlow()

  private val _selectedStop = MutableSharedFlow<Stop>()
  val selectedStop get() = _selectedStop.asSharedFlow()

  private val _searchMode = MutableStateFlow(false)
  val searchMode get() = _searchMode.asStateFlow()

  private val _showNoData = MutableStateFlow(false)
  val showNoData get() = _showNoData.asStateFlow()

  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
    .asStateFlow()

  fun findStops(word: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val locale = locale.value
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
