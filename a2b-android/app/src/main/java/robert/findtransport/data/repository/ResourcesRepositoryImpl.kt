package robert.findtransport.data.repository

import robert.findtransport.data.service.ResourcesService
import robert.findtransport.domain.repository.ResourcesRepository

class ResourcesRepositoryImpl(private val resourcesService: ResourcesService) : ResourcesRepository {
  override val languages: Array<String>
    get() = resourcesService.languageNames
  
  
}
