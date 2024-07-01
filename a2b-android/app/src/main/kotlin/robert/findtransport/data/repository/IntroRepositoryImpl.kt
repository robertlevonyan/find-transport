package robert.findtransport.data.repository

import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.IntroRepository
import robert.findtransport.utils.PREF_INTRO
import javax.inject.Inject

class IntroRepositoryImpl @Inject constructor(private val sharedPreferencesService: SharedPreferencesService) : IntroRepository {
  override val isIntroPassed: Boolean
    get() = sharedPreferencesService.getBoolean(PREF_INTRO, false)

  override fun setIntroPassed() {
    sharedPreferencesService.putBoolean(PREF_INTRO, true)
  }
}
