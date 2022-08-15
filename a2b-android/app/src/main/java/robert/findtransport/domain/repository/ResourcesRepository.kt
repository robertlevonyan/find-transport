package robert.findtransport.domain.repository

import android.graphics.Bitmap

interface ResourcesRepository {
  fun getTransportStopIconBitmap(): Bitmap?

  fun getMetroStopIconBitmap(): Bitmap?
}