package robert.findtransport.domain.usecase.settings

import androidx.appcompat.app.AppCompatDelegate
import robert.findtransport.R
import robert.findtransport.data.model.LanguageData
import robert.findtransport.data.model.SettingData
import robert.findtransport.data.model.ThemeData
import robert.findtransport.domain.repository.SettingsRepository
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import javax.inject.Inject

class SettingsUseCaseImpl @Inject constructor(private val settingsRepository: SettingsRepository) : SettingsUseCase {

  override fun saveLanguage(language: String) =
    settingsRepository.saveLanguage(language)

  override fun saveTheme(theme: Int) =
    settingsRepository.saveTheme(theme)

  override fun saveUpdateDbIfMobile(update: Boolean) =
    settingsRepository.saveUpdateDbIfMobile(update)

  override fun saveReceivePushNotifications(receive: Boolean) =
    settingsRepository.saveReceivePushNotifications(receive)

  override fun getSavedLanguage(): String {
    val languages = settingsRepository.getShortLanguages()

    return when (settingsRepository.getSavedLanguage()) {
      LNG_EN -> languages[1]
      LNG_RU -> languages[2]
      else -> languages[0]
    }
  }

  override fun getAllLanguages(): List<LanguageData> {
    val languages = settingsRepository.getAllLanguages()
    val savedLanguage = getSavedLanguage()
    val shortLanguages = settingsRepository.getShortLanguages()
    val shortLanguagesSettings = settingsRepository.getShortLanguagesSettings()

    return languages.map {
      val shortLng = shortLanguages[languages.indexOf(it)]
      val shortLngSetting = shortLanguagesSettings[languages.indexOf(it)]
      LanguageData(it, shortLng, shortLngSetting, shortLng == savedLanguage)
    }
  }

  override fun getSavedTheme(): Int =
    getThemeNameShort(settingsRepository.getSavedTheme())

  override fun getAllThemes(): List<ThemeData> {
    val themes = settingsRepository.getAllThemes()

    return themes.map { ThemeData(it, getThemeName(it), getThemeNameShort(it), settingsRepository.getSavedTheme() == it) }
  }

  private fun getThemeNameShort(theme: Int): Int =
    when (theme) {
      AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.string.settings_theme_system_short
      AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> R.string.settings_theme_battery_short
      AppCompatDelegate.MODE_NIGHT_NO -> R.string.settings_theme_light
      AppCompatDelegate.MODE_NIGHT_YES -> R.string.settings_theme_dark
      else -> -1
    }

  private fun getThemeName(theme: Int): Int =
    when (theme) {
      AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.string.settings_theme_system
      AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> R.string.settings_theme_battery
      AppCompatDelegate.MODE_NIGHT_NO -> R.string.settings_theme_light
      AppCompatDelegate.MODE_NIGHT_YES -> R.string.settings_theme_dark
      else -> -1
    }

  override fun getSavedUpdateDbIfMobile(): Boolean =
    settingsRepository.getSavedUpdateDbIfMobile()

  override fun getSavedReceivePushNotifications(): Boolean =
    settingsRepository.getSavedReceivePushNotifications()

  override fun getAppVersion(): String =
    settingsRepository.getAppVersion()

  override fun getSettingsList(): List<SettingData> = listOf(
    SettingData(
      type = SettingData.SettingType.LANGUAGE,
      icon = R.drawable.ic_language,
      label = R.string.settings_language,
      detail = R.string.settings_language_details,
      additionalInfo = getSavedLanguage(),
      endViewType = SettingData.EndViewType.IMAGE
    ),
    SettingData(
      SettingData.SettingType.THEME, R.drawable.ic_theme,
      R.string.settings_theme, R.string.settings_theme_details,
      additionalInfo = getSavedTheme(),
      endViewType = SettingData.EndViewType.IMAGE
    ),
    SettingData(
      SettingData.SettingType.UPDATE_CELLULAR, R.drawable.ic_update_over_network,
      R.string.settings_db_update_3g, R.string.settings_db_update_3g_details,
      additionalInfo = getSavedUpdateDbIfMobile(),
      endViewType = SettingData.EndViewType.SWITCH
    ),
    SettingData(
      SettingData.SettingType.PUSH, R.drawable.ic_notifications,
      R.string.settings_notifications, R.string.settings_notifications_details,
      additionalInfo = getSavedReceivePushNotifications(),
      endViewType = SettingData.EndViewType.SWITCH
    ),
    SettingData(
      SettingData.SettingType.CHECK_UPDATE, R.drawable.ic_check_update,
      R.string.settings_check_app_version, R.string.settings_check_app_details,
      additionalInfo = false,
      endViewType = SettingData.EndViewType.PROGRESS
    ),
    SettingData(
      SettingData.SettingType.RATE, R.drawable.ic_star_half,
      R.string.settings_rate, R.string.settings_rate_details,
      additionalInfo = false
    ),
    SettingData(
      SettingData.SettingType.VERSION, R.drawable.ic_info,
      R.string.settings_app_version, R.string.settings_app_version_details,
      additionalInfo = getAppVersion()
    )
  )
}
