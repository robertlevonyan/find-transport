package robert.findtransport.utils.extensions

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import robert.findtransport.base.MainActivity
import android.graphics.Bitmap
import android.graphics.Canvas

import androidx.core.graphics.drawable.DrawableCompat

import android.os.Build

import androidx.core.content.ContextCompat

import android.graphics.drawable.Drawable

fun Context.showToast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
  context?.showToast(message)
}

fun FragmentActivity.fullRecreate() {
  finishAndRemoveTask()
  startActivity(Intent(this, MainActivity::class.java))
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
