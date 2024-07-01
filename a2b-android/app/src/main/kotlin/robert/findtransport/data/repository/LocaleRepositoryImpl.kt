package robert.findtransport.data.repository

import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.PREF_LANGUAGE
import javax.inject.Inject

class LocaleRepositoryImpl @Inject constructor(
  private val sharedPreferencesService: SharedPreferencesService
) : LocaleRepository {

  override fun getCurrentLanguage(): String =
    sharedPreferencesService.getString(PREF_LANGUAGE, LNG_EN)
      ?.takeIf { it.isNotEmpty() }
      ?: LNG_EN

  override fun saveLanguage(language: String) {
    sharedPreferencesService.putString(PREF_LANGUAGE, language)
  }
}
