package robert.findtransport.data.repository

import android.graphics.Bitmap
import robert.findtransport.data.service.ResourcesService
import robert.findtransport.domain.repository.ResourcesRepository
import javax.inject.Inject

class ResourcesRepositoryImpl @Inject constructor(private val resourcesService: ResourcesService) :
    ResourcesRepository {
    override fun getTransportStopIconBitmap(): Bitmap? =
        resourcesService.getTransportStopIconBitmap()

    override fun getMetroStopIconBitmap(): Bitmap? =
        resourcesService.getMetroStopIconBitmap()
}
