package robert.findtransport.domain.usecase.rate

interface RateUseCase {
  fun setRate()

  fun showDialog(): Boolean

  fun updateInterval()

}
