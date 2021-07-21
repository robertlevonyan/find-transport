package robert.findtransport.presentation.component.bottomsheet.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import robert.findtransport.base.BasePickerViewModel
import robert.findtransport.data.model.ThemeData
import robert.findtransport.domain.usecase.settings.SettingsUseCase

class ThemePickerViewModel(settingsUseCase: SettingsUseCase) : BasePickerViewModel<ThemeData>() {
  private val _themesList = MutableLiveData<List<ThemeData>>()
  val themesList: LiveData<List<ThemeData>> get() = _themesList

  private val _selectedTheme = MutableLiveData<ThemeData>()
  val selectedTheme: LiveData<ThemeData> get() = _selectedTheme

  init {
    _themesList.postValue(settingsUseCase.getAllThemes())
  }

  override fun onItemClick(data: ThemeData) {
    _selectedTheme.postValue(data)
  }
}
