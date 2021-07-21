package robert.findtransport.domain.usecase.preference

interface IntroUseCase {
  val isIntroPassed: Boolean
  
  val languages: Array<String>
  
  fun setIntroPassed()
  
}
