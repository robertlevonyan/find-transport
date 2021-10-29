package robert.findtransport.domain.usecase.preference

import robert.findtransport.domain.repository.LocaleRepository
import javax.inject.Inject

class LocaleUseCaseImpl @Inject constructor(private val localeRepository: LocaleRepository) : LocaleUseCase {
  
  override fun getCurrentLanguage(): String =
      localeRepository.getCurrentLanguage()
  
  override fun saveLanguage(language: String) =
      localeRepository.saveLanguage(language)
}
