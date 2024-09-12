package robert.findtransport.domain.usecase.preference

import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import javax.inject.Inject

class LocaleUseCaseImpl @Inject constructor(private val localeRepository: LocaleRepository) :
    LocaleUseCase {

    override fun getCurrentLanguage(): String =
        localeRepository.getCurrentLanguage()

    override fun getCurrentLanguageIndex(): Int = getCurrentLanguage().let { language ->
        when (language) {
            LNG_EN -> 1
            LNG_RU -> 2
            else -> 0
        }
    }

    override fun saveLanguage(language: String) =
        localeRepository.saveLanguage(language)
}
