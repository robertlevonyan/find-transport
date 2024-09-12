package robert.findtransport.domain.usecase.preference

import robert.findtransport.domain.repository.IntroRepository
import javax.inject.Inject

class IntroUseCaseImpl @Inject constructor(private val introRepository: IntroRepository) :
    IntroUseCase {
    override val isIntroPassed: Boolean
        get() = introRepository.isIntroPassed

    override fun setIntroPassed() =
        introRepository.setIntroPassed()

}