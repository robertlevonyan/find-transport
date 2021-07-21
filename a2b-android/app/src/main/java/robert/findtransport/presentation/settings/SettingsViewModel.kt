package robert.findtransport.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
  private val _settingsList = MutableLiveData<List<SettingData>>()
  val settingsList: LiveData<List<SettingData>> get() = _settingsList

  private val _languagePickerEvent = MutableLiveData<Unit>()
  val languagePickerEvent: LiveData<Unit> get() = _languagePickerEvent

  private val _themePickerEvent = MutableLiveData<Unit>()
  val themePickerEvent: LiveData<Unit> get() = _themePickerEvent

  private val _themeSave = MutableLiveData<Unit>()
  val themeSave: LiveData<Unit> get() = _themeSave

  private val _languageSave = MutableLiveData<Unit>()
  val languageSave: LiveData<Unit> get() = _languageSave

  private val _newVersion = MutableLiveData<Unit>()
  val newVersion: LiveData<Unit> get() = _newVersion

  private val _noNewVersion = MutableLiveData<Unit>()
  val noNewVersion: LiveData<Unit> get() = _noNewVersion

  private val _downloadStart = MutableLiveData<Unit>()
  val downloadStart: LiveData<Unit> get() = _downloadStart

  private val _downloadDone = MutableLiveData<Unit>()
  val downloadDone: LiveData<Unit> get() = _downloadDone

  private val _downloadError = MutableLiveData<Unit>()
  val downloadError: LiveData<Unit> get() = _downloadError

  private val _openRate = MutableLiveData<Unit>()
  val openRate: LiveData<Unit> get() = _openRate

  init {
    settings.addAll(listOf(
        SettingData(LANGUAGE, R.drawable.ic_language,
            R.string.settings_language, R.string.settings_language_details,
            additionalInfo = settingsUseCase.getSavedLanguage(),
            endViewType = SettingData.EndViewType.IMAGE),
        SettingData(THEME, R.drawable.ic_theme,
            R.string.settings_theme, R.string.settings_theme_details,
            additionalInfo = settingsUseCase.getSavedTheme(),
            endViewType = SettingData.EndViewType.IMAGE),
        SettingData(UPDATE_CELLULAR, R.drawable.ic_update_over_network,
            R.string.settings_db_update_3g, R.string.settings_db_update_3g_details,
            additionalInfo = settingsUseCase.getSavedUpdateDbIfMobile(),
            endViewType = SettingData.EndViewType.SWITCH),
        SettingData(PUSH, R.drawable.ic_notifications,
            R.string.settings_notifications, R.string.settings_notifications_details,
            additionalInfo = settingsUseCase.getSavedReceivePushNotifications(),
            endViewType = SettingData.EndViewType.SWITCH),
        SettingData(CHECK_UPDATE, R.drawable.ic_check_update,
            R.string.settings_check_app_version, R.string.settings_check_app_details,
            additionalInfo = false,
            endViewType = SettingData.EndViewType.PROGRESS),
        SettingData(RATE, R.drawable.ic_star_half,
            R.string.settings_rate, R.string.settings_rate_details,
            additionalInfo = false),
        SettingData(VERSION, R.drawable.ic_info,
            R.string.settings_app_version, R.string.settings_app_version_details,
            additionalInfo = settingsUseCase.getAppVersion())
    ))

    updateSettingsList(settings)
  }

  private fun updateSettingsList(settings: ArrayList<SettingData>) {
    _settingsList.postValue(settings)
  }

  fun onItemClick(settingData: SettingData) {
    when (settingData.type) {
      LANGUAGE -> _languagePickerEvent.postValue(Unit)
      THEME -> _themePickerEvent.postValue(Unit)
      UPDATE_CELLULAR -> updateBooleanSetting(UPDATE_CELLULAR) { status ->
        settingsUseCase.saveUpdateDbIfMobile(status)
      }
      PUSH -> updateBooleanSetting(PUSH) { status ->
        settingsUseCase.saveReceivePushNotifications(status)
      }
      CHECK_UPDATE -> checkForUpdate()
      RATE -> openRateDialog()
      VERSION -> return
    }
  }

  fun changeLanguage(languageData: LanguageData) {
    settingsUseCase.saveLanguage(languageData.languageShortSetting)
    _languageSave.postValue(Unit)
  }

  fun changeTheme(themeData: ThemeData) {
    themeUseCase.saveTheme(themeData.theme)
    _themeSave.postValue(Unit)
  }

  private fun updateBooleanSetting(settingType: SettingData.SettingType, updateAction: (Boolean) -> Unit) {
    settings.find { it.type == settingType }?.let { updateSetting ->
      updateSetting.additionalInfo?.takeIf { it is Boolean }?.let { additionalInfo ->
        val index = settings.indexOf(updateSetting)
        val status = (additionalInfo as Boolean).inverse()
        updateSetting.additionalInfo = status
        settings[index] = updateSetting
        updateSettingsList(settings)
        updateAction(status)
      }
    }
  }

  private fun checkForUpdate() {
    settings.find { it.type == CHECK_UPDATE }?.let { checkSetting ->
      val index = settings.indexOf(checkSetting)
      checkSetting.additionalInfo = true
      settings[index] = checkSetting
      updateSettingsList(settings)
      viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
          downloadUpdate()
          checkSetting.additionalInfo = false
          settings[index] = checkSetting
          updateSettingsList(settings)
        }
      }
    }
  }

  private fun openRateDialog() {
    _openRate.postValue(Unit)
  }

  fun downloadUpdate() {
    viewModelScope.launch(Dispatchers.IO) {
      _downloadStart.postValue(Unit)
      databaseUseCase.clearDb()
      getStops()
      getTransports()
      delay(1000)
      _downloadDone.postValue(Unit)
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
