package robert.findtransport.data.service

import android.content.Context
import android.graphics.Bitmap
import robert.findtransport.R
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.utils.extensions.getBitmapFromVectorDrawable

class ResourcesService(private val context: Context) {
    fun getExceptionMessage(type: ExceptionType) = when (type) {
        ExceptionType.EMPTY_EMAIL -> R.string.error_empty_email
        ExceptionType.WRONG_EMAIL -> R.string.error_email
        ExceptionType.ERROR_SUBJECT -> R.string.error_subject
        ExceptionType.EMPTY_MESSAGE -> R.string.error_message
        ExceptionType.SHORT_MESSAGE -> R.string.error_message_short
        else -> -1
    }

    fun getTransportStopIconBitmap(): Bitmap? =
        context.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign)

    fun getMetroStopIconBitmap(): Bitmap? =
        context.getBitmapFromVectorDrawable(R.drawable.ic_metro_sign)

}
