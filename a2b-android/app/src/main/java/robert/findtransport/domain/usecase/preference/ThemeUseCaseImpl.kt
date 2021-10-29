package robert.findtransport.domain.usecase.preference

import robert.findtransport.domain.repository.ThemeRepository
import javax.inject.Inject

class ThemeUseCaseImpl @Inject constructor(private val themeRepository: ThemeRepository) : ThemeUseCase {

  override fun saveTheme(theme: Int) {
    themeRepository.saveTheme(theme)
  }
  
  override fun getTheme(): Int =
      themeRepository.getTheme()
}
