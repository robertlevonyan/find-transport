package robert.findtransport.presentation.screens.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.preference.IntroUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.domain.usecase.rate.RateUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  introUseCase: IntroUseCase,
  themeUseCase: ThemeUseCase,
  stopsUseCase: StopsUseCase,
  private val rateUseCase: RateUseCase,
) : BaseViewModel() {
  val introPassed = MutableStateFlow(introUseCase.isIntroPassed).asStateFlow()
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val theme = MutableStateFlow(themeUseCase.getTheme()).asStateFlow()
  val fromStop = MutableStateFlow(Stop.EMPTY)
  val toStop = MutableStateFlow(Stop.EMPTY)
  val showRate = MutableStateFlow(rateUseCase.showDialog())
  val nearbyStop = stopsUseCase.getNearbyStop()

  init {
    rateUseCase.updateInterval()
  }

  fun setFromStop(stop: Stop) {
    viewModelScope.launch { fromStop.emit(stop) }
  }

  fun setToStop(stop: Stop) {
    viewModelScope.launch { toStop.emit(stop) }
  }

  fun swap() {
    val fromValue = fromStop.value
    fromStop.value = toStop.value
    toStop.value = fromValue
  }

  fun openRate() {
    viewModelScope.launch {
      rateUseCase.setRate()
      showRate.value = false
    }
  }

  fun dismissRate() {
    showRate.value = false
  }
}
