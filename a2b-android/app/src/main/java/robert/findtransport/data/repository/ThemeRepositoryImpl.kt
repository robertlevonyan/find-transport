package robert.findtransport.data.repository

import androidx.appcompat.app.AppCompatDelegate
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.ThemeRepository
import robert.findtransport.utils.PREF_THEME

class ThemeRepositoryImpl(
    private val sharedPreferencesService: SharedPreferencesService
) : ThemeRepository {
  
  override fun saveTheme(theme: Int) {
    sharedPreferencesService.putInt(PREF_THEME, theme)
  }
  
  override fun getTheme(): Int =
      sharedPreferencesService.getInt(PREF_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
  
}
