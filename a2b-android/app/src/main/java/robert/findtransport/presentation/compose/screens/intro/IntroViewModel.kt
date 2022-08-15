package robert.findtransport.presentation.compose.screens.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.domain.usecase.preference.IntroUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.utils.LNG_AM
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
  private val introUseCase: IntroUseCase,
  private val localeUseCase: LocaleUseCase
) : BaseViewModel() {
  private val currentLanguageFlow = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val currentLanguage get() = currentLanguageFlow.asStateFlow()

  val currentLanguageIndex = MutableStateFlow(localeUseCase.getCurrentLanguageIndex())
    .asStateFlow()

  fun setIntroPassed() {
    viewModelScope.launch {
      introUseCase.setIntroPassed()
    }
  }

  fun setLanguage(position: Int) {
    val language = when (position) {
      1 -> LNG_EN
      2 -> LNG_RU
      else -> LNG_AM
    }
    viewModelScope.launch {
      currentLanguageFlow.value = language
      localeUseCase.saveLanguage(language)
    }
  }
}
