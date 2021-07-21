package robert.findtransport.domain.usecase.preference

interface ThemeUseCase {

  fun saveTheme(theme: Int)
  
  fun getTheme(): Int
}
