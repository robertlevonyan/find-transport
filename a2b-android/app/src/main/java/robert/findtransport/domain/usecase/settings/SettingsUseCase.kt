package robert.findtransport.domain.usecase.settings

import robert.findtransport.data.model.LanguageData
import robert.findtransport.data.model.SettingData
import robert.findtransport.data.model.ThemeData

interface SettingsUseCase {
  fun saveLanguage(language: String)

  fun saveTheme(theme: Int)

  fun saveUpdateDbIfMobile(update: Boolean)

  fun saveReceivePushNotifications(receive: Boolean)

  fun getSavedLanguage(): String

  fun getAllLanguages(): List<LanguageData>

  fun getSavedTheme(): Int

  fun getAllThemes(): List<ThemeData>

  fun getSavedUpdateDbIfMobile(): Boolean

  fun getSavedReceivePushNotifications(): Boolean

  fun getAppVersion(): String

  fun getSettingsList(): List<SettingData>
}
