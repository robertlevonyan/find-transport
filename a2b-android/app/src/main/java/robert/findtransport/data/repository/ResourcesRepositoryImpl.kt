package robert.findtransport.data.repository

import android.graphics.Bitmap
import robert.findtransport.data.service.ResourcesService
import robert.findtransport.domain.repository.ResourcesRepository

class ResourcesRepositoryImpl(
  private val resourcesService: ResourcesService,
) : ResourcesRepository {
  override val languages: Array<String>
    get() = resourcesService.languageNames

  override fun getTransportStopIconBitmap(): Bitmap? =
    resourcesService.getTransportStopIconBitmap()

  override fun getMetroStopIconBitmap(): Bitmap? =
    resourcesService.getMetroStopIconBitmap()
}
