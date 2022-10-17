package robert.findtransport.utils.extensions

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(message: String, action: String = "", actionClick: (View) -> Unit = {}): Snackbar {
  return Snackbar.make(this, message, if (action == "") Snackbar.LENGTH_SHORT else Snackbar.LENGTH_LONG)
    .setAction(action, actionClick).apply {
      show()
    }
}

fun View.showSnackbar(message: Int): Snackbar {
  return Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply { show() }
}

fun View.showInfiniteSnackbar(message: Int): Snackbar {
  return Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).apply { show() }
}

@SuppressLint("RestrictedApi")
fun View.onWindowInsets(action: (View, WindowInsetsCompat) -> Unit) {
  requestApplyInsetsWhenAttached()
  ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
    action(v, insets)
    insets
  }
}

fun View.requestApplyInsetsWhenAttached() {
  if (isAttachedToWindow) {
    ViewCompat.requestApplyInsets(this)
  } else {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewDetachedFromWindow(v: View) = Unit

      override fun onViewAttachedToWindow(v: View) {
        v.removeOnAttachStateChangeListener(this)
        ViewCompat.requestApplyInsets(v)
      }
    })
  }
}

fun Window.fitSystemWindows() {
  WindowCompat.setDecorFitsSystemWindows(this, false)
}

infix fun ImageView.set(@DrawableRes id: Int) {
  setImageResource(id)
}

infix fun ImageView.set(bitmap: Bitmap) {
  setImageBitmap(bitmap)
}

infix fun ImageView.set(drawable: Drawable) {
  setImageDrawable(drawable)
}

infix fun TextView.set(@StringRes id: Int) {
  setText(id)
}

infix fun TextView.set(text: String) {
  setText(text)
}

fun ViewPropertyAnimator.doOnEnd(onEnd: () -> Unit) {
  setListener(object : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator) = Unit

    override fun onAnimationEnd(animation: Animator) = onEnd()

    override fun onAnimationCancel(animation: Animator) = Unit

    override fun onAnimationStart(animation: Animator) = Unit
  })
}

var View.topMargin: Int
  get() = (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin
  set(value) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    topMargin = value
  }

var View.bottomMargin: Int
  get() = (this.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
  set(value) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    bottomMargin = value
  }

var View.bottomPadding: Int
  get() = paddingBottom
  set(value) = setPaddingRelative(paddingStart, paddingTop, paddingEnd, value)

var View.topPadding: Int
  get() = paddingTop
  set(value) = setPaddingRelative(paddingStart, value, paddingEnd, paddingBottom)
