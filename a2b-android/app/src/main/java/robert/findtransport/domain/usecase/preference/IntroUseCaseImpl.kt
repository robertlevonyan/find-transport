package robert.findtransport.domain.usecase.preference

import robert.findtransport.domain.repository.IntroRepository
import robert.findtransport.domain.repository.ResourcesRepository
import javax.inject.Inject

class IntroUseCaseImpl @Inject constructor(
    private val introRepository: IntroRepository,
    private val resourcesRepository: ResourcesRepository,
) : IntroUseCase {
  override val isIntroPassed: Boolean
    get() = introRepository.isIntroPassed
  
  override val languages: Array<String>
    get() = resourcesRepository.languages
  
  override fun setIntroPassed() =
      introRepository.setIntroPassed()
  
}