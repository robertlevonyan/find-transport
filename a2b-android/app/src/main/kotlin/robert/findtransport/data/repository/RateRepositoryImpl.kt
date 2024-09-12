package robert.findtransport.data.repository

import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.repository.RateRepository
import robert.findtransport.utils.PREF_RATE
import robert.findtransport.utils.PREF_RATE_INTERVAL
import javax.inject.Inject

class RateRepositoryImpl @Inject constructor(private val sharedPreferencesService: SharedPreferencesService) :
    RateRepository {
    override var isRate: Boolean
        get() = sharedPreferencesService.getBoolean(PREF_RATE, false)
        set(value) = sharedPreferencesService.putBoolean(PREF_RATE, value)

    override var rateIntervalCount: Int
        get() = sharedPreferencesService.getInt(PREF_RATE_INTERVAL, 0)
        set(value) = sharedPreferencesService.putInt(PREF_RATE_INTERVAL, value)
}
