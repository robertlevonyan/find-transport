package robert.findtransport.domain.usecase.preference

import robert.findtransport.domain.repository.LocaleRepository

class LocaleUseCaseImpl(private val localeRepository: LocaleRepository) : LocaleUseCase {
  
  override fun getCurrentLanguage(): String =
      localeRepository.getCurrentLanguage()
  
  override fun saveLanguage(language: String) =
      localeRepository.saveLanguage(language)
}
