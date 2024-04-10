package robert.findtransport.presentation.screens.stops

import android.location.Address
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopWithAddress
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
        .cachedIn(scope = this)
        .filterNotNull()
        .collectLatest {
          allStops.emit(it)
        }
    }
  }

  suspend fun getAddress(stop: Stop): Address? {
      return stopsUseCase.getAddress(stop)
  }
}
