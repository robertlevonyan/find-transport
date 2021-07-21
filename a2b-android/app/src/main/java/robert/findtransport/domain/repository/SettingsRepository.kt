package robert.findtransport.domain.repository

interface SettingsRepository {
  
  fun saveLanguage(language: String)
  
  fun saveTheme(theme: Int)
  
  fun saveUpdateDbIfMobile(update: Boolean)
  
  fun saveReceivePushNotifications(receive: Boolean)
  
  fun getSavedLanguage(): String
  
  fun getAllLanguages(): Array<String>
  
  fun getShortLanguages(): Array<String>
  
  fun getShortLanguagesSettings(): Array<String>
  
  fun getSavedTheme(): Int
  
  fun getAllThemes(): Array<Int>
  
  fun getShortThemes(): Array<Int>
  
  fun getSavedUpdateDbIfMobile(): Boolean
  
  fun getSavedReceivePushNotifications(): Boolean
  
  fun getAppVersion(): String
  
}
