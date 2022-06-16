package robert.findtransport.presentation.compose.screens.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
  private val _languageChanged = MutableSharedFlow<String>()
  val languageChanged: Flow<String> get() = _languageChanged

  private val currentLanguageFlow = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val currentLanguage get() = currentLanguageFlow.asStateFlow()

  val currentLanguageIndex = MutableStateFlow(localeUseCase.getCurrentLanguageIndex())
    .asStateFlow()

//  private val _pickerArmValue = MutableStateFlow(false)
//  val pickerArmValue: Flow<Boolean> get() = _pickerArmValue
//
//  private val _pickerEngValue = MutableStateFlow(false)
//  val pickerEngValue: Flow<Boolean> get() = _pickerEngValue
//
//  private val _pickerRusValue = MutableStateFlow(false)
//  val pickerRusValue: Flow<Boolean> get() = _pickerRusValue

  private val _introPassed = MutableSharedFlow<Unit>()
  val introPassed: Flow<Unit> get() = _introPassed

//  init {
//    viewModelScope.launch {
//      when () {
//        LNG_EN -> _pickerEngValue.emit(true)
//        LNG_RU -> _pickerRusValue.emit(true)
//        else -> _pickerArmValue.emit(true)
//      }
//    }
//  }

  fun setIntroPassed() {
    viewModelScope.launch {
      introUseCase.setIntroPassed()
      _introPassed.emit(Unit)
    }
  }

  fun setLanguage(position: Int) {
    val language = when (position) {
      1 -> LNG_EN
      2 -> LNG_RU
      else -> LNG_AM
    }
    viewModelScope.launch {
      _languageChanged.emit(language)
      currentLanguageFlow.value = language
      localeUseCase.saveLanguage(language)
    }
  }
}
