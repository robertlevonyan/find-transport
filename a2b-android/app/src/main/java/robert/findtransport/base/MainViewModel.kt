package robert.findtransport.base

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.utils.PREF_LANGUAGE
import robert.findtransport.utils.PREF_THEME
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  themeUseCase: ThemeUseCase,
  localeUseCase: LocaleUseCase,
) : BaseViewModel() {
  val theme = MutableStateFlow(themeUseCase.getTheme())
  val currentLanguage = MutableStateFlow(localeUseCase.getCurrentLanguage())

  init {
    viewModelScope.launch {
      launch {
        SharedPreferencesService.getPreferenceChangedValue<Int>(PREF_THEME).collectLatest { value ->
          theme.value = value
        }
      }
      launch {
        SharedPreferencesService.getPreferenceChangedValue<String>(PREF_LANGUAGE).collectLatest { value ->
          currentLanguage.value = value
        }
      }
    }
  }
}