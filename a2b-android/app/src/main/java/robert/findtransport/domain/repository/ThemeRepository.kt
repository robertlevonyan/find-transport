package robert.findtransport.domain.repository

interface ThemeRepository {
  
  fun saveTheme(theme: Int)
  
  fun getTheme(): Int
  
}