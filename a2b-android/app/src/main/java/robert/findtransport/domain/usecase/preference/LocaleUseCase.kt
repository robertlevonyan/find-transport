package robert.findtransport.domain.usecase.preference

interface LocaleUseCase {
  
  fun getCurrentLanguage(): String
  
  fun saveLanguage(language: String)
}
