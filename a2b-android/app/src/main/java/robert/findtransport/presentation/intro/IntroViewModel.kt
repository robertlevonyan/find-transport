package robert.findtransport.presentation.intro

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.domain.usecase.preference.IntroUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.utils.LNG_AM
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU

class IntroViewModel(
  private val introUseCase: IntroUseCase,
  private val localeUseCase: LocaleUseCase
) : BaseViewModel() {
  private val _languageChanged = MutableSharedFlow<String>()
  val languageChanged: Flow<String> get() = _languageChanged

  private val _pickerArmValue = MutableSharedFlow<Unit>()
  val pickerArmValue: Flow<Unit> get() = _pickerArmValue

  private val _pickerEngValue = MutableSharedFlow<Unit>()
  val pickerEngValue: Flow<Unit> get() = _pickerEngValue

  private val _pickerRusValue = MutableSharedFlow<Unit>()
  val pickerRusValue: Flow<Unit> get() = _pickerRusValue

  private val _introPassed = MutableSharedFlow<Unit>()
  val introPassed: Flow<Unit> get() = _introPassed

  init {
    viewModelScope.launch {
      when (localeUseCase.getCurrentLanguage()) {
        LNG_EN -> _pickerEngValue.emit(Unit)
        LNG_RU -> _pickerRusValue.emit(Unit)
        else -> _pickerArmValue.emit(Unit)
      }
    }
  }

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
      localeUseCase.saveLanguage(language)
    }
  }
}
