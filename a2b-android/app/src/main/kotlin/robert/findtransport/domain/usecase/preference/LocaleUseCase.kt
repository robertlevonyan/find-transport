package robert.findtransport.domain.usecase.preference

interface LocaleUseCase {
  
  fun getCurrentLanguage(): String

  fun getCurrentLanguageIndex(): Int

  fun saveLanguage(language: String)
}
