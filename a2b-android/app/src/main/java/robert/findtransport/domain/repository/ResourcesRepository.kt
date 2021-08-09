package robert.findtransport.domain.repository

import android.graphics.Bitmap

interface ResourcesRepository {
  val languages: Array<String>

  fun getTransportStopIconBitmap(): Bitmap?

  fun getMetroStopIconBitmap(): Bitmap?
}