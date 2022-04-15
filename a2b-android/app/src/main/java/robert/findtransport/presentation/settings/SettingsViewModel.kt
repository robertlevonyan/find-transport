package robert.findtransport.presentation.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.LanguageData
import robert.findtransport.data.model.SettingData
import robert.findtransport.data.model.SettingData.SettingType.*
import robert.findtransport.data.model.ThemeData
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.domain.usecase.settings.SettingsUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.extensions.inverse
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val settingsUseCase: SettingsUseCase,
  private val themeUseCase: ThemeUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val databaseUseCase: DatabaseUseCase
) : BaseViewModel() {
  private val _themeSave = MutableSharedFlow<Unit>()
  val themeSave: Flow<Unit> get() = _themeSave

  private val _languageSave = MutableSharedFlow<Unit>()
  val languageSave: Flow<Unit> get() = _languageSave

  private val _newVersion = MutableSharedFlow<Unit>()
  val newVersion: Flow<Unit> get() = _newVersion

  private val _noNewVersion = MutableSharedFlow<Unit>()
  val noNewVersion: Flow<Unit> get() = _noNewVersion

  private val _downloadStart = MutableSharedFlow<Unit>()
  val downloadStart: Flow<Unit> get() = _downloadStart

  private val _downloadDone = MutableSharedFlow<Unit>()
  val downloadDone: Flow<Unit> get() = _downloadDone

  private val _downloadError = MutableSharedFlow<Unit>()
  val downloadError: Flow<Unit> get() = _downloadError

  private val _settingsList = MutableStateFlow(settingsUseCase.getSettingsList())
  val settingsList: StateFlow<List<SettingData>> get() = _settingsList

  fun changeLanguage(languageData: LanguageData) {
    viewModelScope.launch {
      settingsUseCase.saveLanguage(languageData.languageShortSetting)
      _languageSave.emit(Unit)
    }
  }

  fun changeTheme(themeData: ThemeData) {
    viewModelScope.launch {
      themeUseCase.saveTheme(themeData.theme)
      _themeSave.emit(Unit)
    }
  }

  fun updateCellular() {
    updateBooleanSetting(UPDATE_CELLULAR) { status ->
      settingsUseCase.saveUpdateDbIfMobile(status)
    }
  }

  fun updatePush() {
    updateBooleanSetting(PUSH) { status ->
      settingsUseCase.saveReceivePushNotifications(status)
    }
  }

  private fun updateBooleanSetting(settingType: SettingData.SettingType, updateAction: (Boolean) -> Unit) {
    viewModelScope.launch {
      val settings = _settingsList.value.toMutableList()

      settings.find { it.type == settingType }?.let { updateSetting ->
        updateSetting.additionalInfo?.takeIf { it is Boolean }?.let { additionalInfo ->
          val index = settings.indexOf(updateSetting)
          val status = (additionalInfo as Boolean).inverse()
          updateSetting.additionalInfo = status
          settings[index] = updateSetting
          _settingsList.value = settings
          updateAction(status)
        }
      }
    }
  }

  fun checkForUpdate() {
    viewModelScope.launch {
      val settings = _settingsList.value.toMutableList()
      settings.find { it.type == CHECK_UPDATE }?.let { checkSetting ->
        val index = settings.indexOf(checkSetting)
        checkSetting.additionalInfo = true
        settings[index] = checkSetting
        _settingsList.emit(settings)
        withContext(Dispatchers.IO) {
          withContext(Dispatchers.Main) {
            downloadUpdate()
            checkSetting.additionalInfo = false
            settings[index] = checkSetting
            _settingsList.value = settings
          }
        }
      }
    }
  }

  fun downloadUpdate() {
    viewModelScope.launch(Dispatchers.IO) {
      _downloadStart.emit(Unit)
      databaseUseCase.clearDb()
      getStops()
      getTransports()
      delay(1000)
      _downloadDone.emit(Unit)
    }
  }

  private suspend fun getStops() = withContext(Dispatchers.IO) {
    stopsUseCase.downloadStops()
    stopsUseCase.downloadLocations()
  }

  private suspend fun getTransports() = withContext(Dispatchers.IO) {
    transportUseCase.downloadTransports()
    transportUseCase.downloadJoins()
  }
}
