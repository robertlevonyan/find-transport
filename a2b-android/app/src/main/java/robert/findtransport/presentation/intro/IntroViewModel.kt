package robert.findtransport.presentation.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
  private val _languageChanged = MutableLiveData<String>()
  val languageChanged: LiveData<String> get() = _languageChanged

  private val _languages = MutableLiveData<Array<String>>()

  private val _pickerArmValue = MutableLiveData<Unit>()
  val pickerArmValue: LiveData<Unit> get() = _pickerArmValue

  private val _pickerEngValue = MutableLiveData<Unit>()
  val pickerEngValue: LiveData<Unit> get() = _pickerEngValue

  private val _pickerRusValue = MutableLiveData<Unit>()
  val pickerRusValue: LiveData<Unit> get() = _pickerRusValue

  private val _introPassed = MutableLiveData<Unit>()
  val introPassed: LiveData<Unit> get() = _introPassed

  init {
    _languages.postValue(introUseCase.languages)
    when (localeUseCase.getCurrentLanguage()) {
      LNG_EN -> _pickerEngValue.postValue(Unit)
      LNG_RU -> _pickerRusValue.postValue(Unit)
      else -> _pickerArmValue.postValue(Unit)
    }
  }

  fun setIntroPassed() {
    introUseCase.setIntroPassed()
    _introPassed.postValue(Unit)
  }

  fun setLanguage(position: Int) {
    val language = when (position) {
      1 -> LNG_EN
      2 -> LNG_RU
      else -> LNG_AM
    }
    _languageChanged.postValue(language)
    localeUseCase.saveLanguage(language)
  }
}
