package robert.findtransport.domain.repository

interface LocaleRepository {
  fun getCurrentLanguage(): String
  
  fun saveLanguage(language: String)
  
}
