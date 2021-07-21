package robert.findtransport.data.repository

import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.PREF_LANGUAGE

class LocaleRepositoryImpl(
    private val preferencesService: SharedPreferencesService
) : LocaleRepository {
  
  override fun getCurrentLanguage(): String = preferencesService.getString(PREF_LANGUAGE, LNG_EN)?.takeIf { it.isNotEmpty() }
      ?: LNG_EN
  
  override fun saveLanguage(language: String) {
    preferencesService.putString(PREF_LANGUAGE, language)
  }
  
}