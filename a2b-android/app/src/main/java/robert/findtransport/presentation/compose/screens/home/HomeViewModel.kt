package robert.findtransport.presentation.compose.screens.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.usecase.preference.IntroUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.utils.PREF_INTRO
import robert.findtransport.utils.PREF_THEME
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  introUseCase: IntroUseCase,
  themeUseCase: ThemeUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val introPassed = MutableStateFlow(introUseCase.isIntroPassed).asStateFlow()
  val theme = MutableStateFlow(themeUseCase.getTheme())

  init {
    viewModelScope.launch {
      SharedPreferencesService.getPreferenceChangedValue<Int>(PREF_THEME).collectLatest { value ->
        println(value)
        theme.value = value
      }
    }
  }

  private val fromStopFlow = MutableStateFlow(Stop.EMPTY)
  val fromStop get() = fromStopFlow.asStateFlow()

  private val toStopFlow = MutableStateFlow(Stop.EMPTY)
  val toStop get() = toStopFlow.asStateFlow()

  fun setFromStop(stop: Stop) {
    viewModelScope.launch { fromStopFlow.emit(stop) }
  }

  fun setToStop(stop: Stop) {
    viewModelScope.launch { toStopFlow.emit(stop) }
  }

  fun swap() {
    val fromValue = fromStopFlow.value
    fromStopFlow.value = toStopFlow.value
    toStopFlow.value = fromValue
  }
}
