package robert.findtransport.domain.usecase.rate

import robert.findtransport.domain.repository.RateRepository

class RateUseCaseImpl(private val rateRepository: RateRepository) : RateUseCase {
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