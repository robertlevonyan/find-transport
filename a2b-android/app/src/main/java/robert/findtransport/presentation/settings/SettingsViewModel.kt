package robert.findtransport.presentation.settings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import robert.findtransport.R
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

class SettingsViewModel(
  private val settingsUseCase: SettingsUseCase,
  private val themeUseCase: ThemeUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val databaseUseCase: DatabaseUseCase
) : BaseViewModel() {
  private val settings: ArrayList<SettingData> = arrayListOf()

  private val _languagePickerEvent = MutableSharedFlow<Unit>()
  val languagePickerEvent: Flow<Unit> get() = _languagePickerEvent

  private val _themePickerEvent = MutableSharedFlow<Unit>()
  val themePickerEvent: Flow<Unit> get() = _themePickerEvent

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

  private val _openRate = MutableSharedFlow<Unit>()
  val openRate: Flow<Unit> get() = _openRate

  init {
    settings.addAll(
      listOf(
        SettingData(
          LANGUAGE, R.drawable.ic_language,
          R.string.settings_language, R.string.settings_language_details,
          additionalInfo = settingsUseCase.getSavedLanguage(),
          endViewType = SettingData.EndViewType.IMAGE
        ),
        SettingData(
          THEME, R.drawable.ic_theme,
          R.string.settings_theme, R.string.settings_theme_details,
          additionalInfo = settingsUseCase.getSavedTheme(),
          endViewType = SettingData.EndViewType.IMAGE
        ),
        SettingData(
          UPDATE_CELLULAR, R.drawable.ic_update_over_network,
          R.string.settings_db_update_3g, R.string.settings_db_update_3g_details,
          additionalInfo = settingsUseCase.getSavedUpdateDbIfMobile(),
          endViewType = SettingData.EndViewType.SWITCH
        ),
        SettingData(
          PUSH, R.drawable.ic_notifications,
          R.string.settings_notifications, R.string.settings_notifications_details,
          additionalInfo = settingsUseCase.getSavedReceivePushNotifications(),
          endViewType = SettingData.EndViewType.SWITCH
        ),
        SettingData(
          CHECK_UPDATE, R.drawable.ic_check_update,
          R.string.settings_check_app_version, R.string.settings_check_app_details,
          additionalInfo = false,
          endViewType = SettingData.EndViewType.PROGRESS
        ),
        SettingData(
          RATE, R.drawable.ic_star_half,
          R.string.settings_rate, R.string.settings_rate_details,
          additionalInfo = false
        ),
        SettingData(
          VERSION, R.drawable.ic_info,
          R.string.settings_app_version, R.string.settings_app_version_details,
          additionalInfo = settingsUseCase.getAppVersion()
        )
      )
    )
  }

  private val _settingsList = MutableStateFlow<List<SettingData>>(settings)
  val settingsList: Flow<List<SettingData>> get() = _settingsList

  fun onItemClick(settingData: SettingData) {
    viewModelScope.launch {
      when (settingData.type) {
        LANGUAGE -> _languagePickerEvent.emit(Unit)
        THEME -> _themePickerEvent.emit(Unit)
        UPDATE_CELLULAR -> updateBooleanSetting(UPDATE_CELLULAR) { status ->
          settingsUseCase.saveUpdateDbIfMobile(status)
        }
        PUSH -> updateBooleanSetting(PUSH) { status ->
          settingsUseCase.saveReceivePushNotifications(status)
        }
        CHECK_UPDATE -> checkForUpdate()
        RATE -> openRateDialog()
        VERSION -> return@launch
      }
    }
  }

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

  private fun updateBooleanSetting(settingType: SettingData.SettingType, updateAction: (Boolean) -> Unit) {
    viewModelScope.launch {
      settings.find { it.type == settingType }?.let { updateSetting ->
        updateSetting.additionalInfo?.takeIf { it is Boolean }?.let { additionalInfo ->
          val index = settings.indexOf(updateSetting)
          val status = (additionalInfo as Boolean).inverse()
          updateSetting.additionalInfo = status
          settings[index] = updateSetting
          _settingsList.emit(settings)
          updateAction(status)
        }
      }
    }
  }

  private fun checkForUpdate() {
    viewModelScope.launch {
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
            _settingsList.emit(settings)
          }
        }
      }
    }
  }

  private fun openRateDialog() {
    viewModelScope.launch {
      _openRate.emit(Unit)
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
