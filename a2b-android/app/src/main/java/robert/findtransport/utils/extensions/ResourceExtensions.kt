package robert.findtransport.utils.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import robert.findtransport.base.BaseFragment

fun Context.getColorFromRes(@ColorRes clr: Int): Int {
  return ContextCompat.getColor(this, clr)
}

fun Context.getDimen(@DimenRes dmn: Int): Float {
  return resources.getDimension(dmn)
}

fun Context.getDimenInt(@DimenRes dmn: Int): Int {
  return resources.getDimensionPixelSize(dmn)
}

fun BaseFragment<*, *>.getDimenInt(@DimenRes dmn: Int): Int {
  return context?.getDimenInt(dmn) ?: 0
}
