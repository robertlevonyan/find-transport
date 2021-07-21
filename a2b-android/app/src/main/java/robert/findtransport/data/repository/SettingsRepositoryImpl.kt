package robert.findtransport.data.repository

import robert.findtransport.BuildConfig
import robert.findtransport.data.service.ResourcesService
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.domain.repository.SettingsRepository
import robert.findtransport.domain.repository.ThemeRepository
import robert.findtransport.utils.PREF_NOTIFICATIONS
import robert.findtransport.utils.PREF_UPDATE_NETWORK

class SettingsRepositoryImpl(
    private val localeRepository: LocaleRepository,
    private val themeRepository: ThemeRepository,
    private val sharedPreferencesService: SharedPreferencesService,
    private val resourcesService: ResourcesService
) : SettingsRepository {
  
  override fun saveLanguage(language: String) {
    localeRepository.saveLanguage(language)
  }
  
  override fun saveTheme(theme: Int) {
    themeRepository.saveTheme(theme)
  }
  
  override fun saveUpdateDbIfMobile(update: Boolean) {
    sharedPreferencesService.putBoolean(PREF_UPDATE_NETWORK, update)
  }
  
  override fun saveReceivePushNotifications(receive: Boolean) {
    sharedPreferencesService.putBoolean(PREF_NOTIFICATIONS, receive)
  }
  
  override fun getSavedLanguage(): String =
      localeRepository.getCurrentLanguage()
  
  override fun getAllLanguages(): Array<String> =
      resourcesService.languageNames
  
  override fun getShortLanguages(): Array<String> =
      resourcesService.languageShortNames
  
  override fun getShortLanguagesSettings(): Array<String> =
      resourcesService.languageShortForSettings
  
  override fun getSavedTheme(): Int =
      themeRepository.getTheme()
  
  override fun getAllThemes(): Array<Int> =
      resourcesService.themeNames
  
  override fun getShortThemes(): Array<Int> =
      resourcesService.themeShortNames
  
  override fun getSavedUpdateDbIfMobile(): Boolean =
      sharedPreferencesService.getBoolean(PREF_UPDATE_NETWORK, false)
  
  override fun getSavedReceivePushNotifications(): Boolean =
      sharedPreferencesService.getBoolean(PREF_NOTIFICATIONS, true)
  
  override fun getAppVersion(): String =
      BuildConfig.VERSION_NAME
  
}
