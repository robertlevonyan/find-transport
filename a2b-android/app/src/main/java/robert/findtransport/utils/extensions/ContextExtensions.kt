package robert.findtransport.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Context.showToast(message: Int) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
  val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
  val bitmap = Bitmap.createBitmap(
    drawable.intrinsicWidth,
    drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
  )
  val canvas = Canvas(bitmap)
  drawable.setBounds(0, 0, canvas.width, canvas.height)
  drawable.draw(canvas)
  return bitmap
}
