package robert.findtransport.data.repository

import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.RateRepository
import robert.findtransport.utils.PREF_RATE
import robert.findtransport.utils.PREF_RATE_INTERVAL

class RateRepositoryImpl(private val preferencesService: SharedPreferencesService) : RateRepository {
  override var isRate: Boolean
    get() = preferencesService.getBoolean(PREF_RATE, false)
    set(value) = preferencesService.putBoolean(PREF_RATE, value)

  override var rateIntervalCount: Int
    get() = preferencesService.getInt(PREF_RATE_INTERVAL, 0)
    set(value) = preferencesService.putInt(PREF_RATE_INTERVAL, value)
}