package robert.findtransport.utils

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {
  override fun updateMeasureState(textPaint: TextPaint) {
    applyCustomTypeFace(textPaint, typeface)
  }

  override fun updateDrawState(tp: TextPaint?) {
    applyCustomTypeFace(tp, typeface)
  }

  private fun applyCustomTypeFace(paint: Paint?, tf: Typeface) {
    paint?.typeface = tf
  }
}
