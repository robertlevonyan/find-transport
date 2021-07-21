package robert.findtransport.utils.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import robert.findtransport.base.BaseFragment

fun Context.getColorFromRes(@ColorRes clr: Int): Int {
  return ContextCompat.getColor(this, clr)
}

fun BaseFragment<*, *>.getColorFromRes(@ColorRes clr: Int): Int {
  return requireContext().getColorFromRes(clr)
}

fun Context.getDrawableFromRes(@DrawableRes drw: Int): Drawable? {
  return AppCompatResources.getDrawable(this, drw)
}

fun BaseFragment<*, *>.getDrawableFromRes(@DrawableRes drw: Int): Drawable? {
  return context?.getDrawableFromRes(drw)
}

fun Context.gerDimenFromRes(@DimenRes dmn: Int): Float {
  return resources.getDimension(dmn)
}

fun BaseFragment<*, *>.gerDimenFromRes(@DimenRes dmn: Int): Float {
  return requireContext().gerDimenFromRes(dmn)
}

fun Context.getDimen(@DimenRes dmn: Int): Float {
  return resources.getDimension(dmn)
}

fun Context.getDimenInt(@DimenRes dmn: Int): Int {
  return resources.getDimensionPixelSize(dmn)
}

fun BaseFragment<*, *>.getDimenInt(@DimenRes dmn: Int): Int {
  return requireContext().getDimenInt(dmn)
}
