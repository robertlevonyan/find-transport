package robert.findtransport.utils.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat

fun Context.getColorFromRes(@ColorRes clr: Int): Int {
  return ContextCompat.getColor(this, clr)
}

fun Context.getDimenInt(@DimenRes dmn: Int): Int {
  return resources.getDimensionPixelSize(dmn)
}
