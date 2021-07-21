package robert.findtransport.data.repository

import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.IntroRepository
import robert.findtransport.utils.PREF_INTRO

class IntroRepositoryImpl(private val preferencesService: SharedPreferencesService) : IntroRepository {
  override val isIntroPassed: Boolean
    get() = preferencesService.getBoolean(PREF_INTRO, false)
  
  override fun setIntroPassed() {
    preferencesService.putBoolean(PREF_INTRO, true)
  }
  
}
