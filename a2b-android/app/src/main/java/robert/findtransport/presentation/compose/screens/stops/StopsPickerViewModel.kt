package robert.findtransport.presentation.compose.screens.stops

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
  localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val allStops: Flow<PagingData<Stop>> = stopsUseCase.getStopsPaged()
    .cachedIn(viewModelScope + Dispatchers.IO)

  fun findStops(word: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val locale = locale.value
      val stops = stopsUseCase.getStopsAutocomplete(word, locale)
      withContext(Dispatchers.Main) {
//        if (stops.isEmpty() && word.isEmpty()) {
//          _showNoData.value = false
//        } else if (stops.isEmpty() && word.isNotEmpty()) {
//          _autocompleteStops.emit(stops)
//          _showNoData.value = true
//        } else {
//          _autocompleteStops.emit(stops)
//          _showNoData.value = false
//        }
      }
    }
  }
}
