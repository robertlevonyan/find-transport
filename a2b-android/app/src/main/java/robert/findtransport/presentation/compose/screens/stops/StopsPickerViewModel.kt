package robert.findtransport.presentation.compose.screens.stops

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
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
  val allStops: MutableSharedFlow<PagingData<Stop>> = MutableSharedFlow()

  fun findStops(word: String) {
    viewModelScope.launch(Dispatchers.IO) {
      stopsUseCase.getStopsPaged(word, locale.value)
        .cachedIn(this)
        .collectLatest {
          allStops.emit(it)
        }
    }
  }
}
