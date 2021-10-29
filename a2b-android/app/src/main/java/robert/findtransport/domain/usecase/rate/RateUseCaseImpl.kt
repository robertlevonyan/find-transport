package robert.findtransport.domain.usecase.rate

import robert.findtransport.domain.repository.RateRepository
import javax.inject.Inject

class RateUseCaseImpl @Inject constructor(private val rateRepository: RateRepository) : RateUseCase {
  override fun setRate() {
    rateRepository.isRate = true
  }

  override fun showDialog(): Boolean =
      if (rateRepository.isRate) false
      else rateRepository.rateIntervalCount > 0 && rateRepository.rateIntervalCount % 7 == 0

  override fun updateInterval() {
    rateRepository.rateIntervalCount++
  }
}