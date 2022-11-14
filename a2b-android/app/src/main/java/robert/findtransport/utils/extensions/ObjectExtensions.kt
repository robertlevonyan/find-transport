package robert.findtransport.utils.extensions

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import android.util.Patterns
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

fun String.isEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

@SuppressLint("LogNotTimber")
suspend fun <R> makeApiCall(call: suspend () -> R) = try {
  Result.Success(call())
} catch (e: Exception) {
  Log.e("A2B", "ERROR", e)
  Result.Error(A2bException(ExceptionType.API, -1, e))
}

fun isTablet(): Boolean {
  val metrics = Resources.getSystem().displayMetrics

  val yInches = metrics.heightPixels / metrics.ydpi
  val xInches = metrics.widthPixels / metrics.xdpi
  val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
  return diagonalInches >= 6.8
}

fun Date.format(): String = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(this)
