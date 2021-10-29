package robert.findtransport.presentation.component.bottomsheet.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import robert.findtransport.base.BasePickerViewModel
import robert.findtransport.data.model.LanguageData
import robert.findtransport.domain.usecase.settings.SettingsUseCase
import javax.inject.Inject

@HiltViewModel
class LanguagePickerViewModel @Inject constructor(private val settingsUseCase: SettingsUseCase) : BasePickerViewModel<LanguageData>() {
  private val _languagesList = MutableLiveData<List<LanguageData>>()
  val languagesList: LiveData<List<LanguageData>> get() = _languagesList

  private val _selectedLanguage = MutableLiveData<LanguageData>()
  val selectedLanguage: LiveData<LanguageData> get() = _selectedLanguage

  init {
    _languagesList.postValue(settingsUseCase.getAllLanguages())
  }

  override fun onItemClick(data: LanguageData) {
    _selectedLanguage.postValue(data)
  }
}
