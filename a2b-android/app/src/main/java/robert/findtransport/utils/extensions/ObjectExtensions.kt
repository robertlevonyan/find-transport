package robert.findtransport.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import android.util.Patterns
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
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

fun Context.isNightMode(): Boolean =
  (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

fun <T> List<T>.asPairs(): List<Pair<T, T?>> {
  val pairs = mutableListOf<Pair<T, T?>>()
  for (i in 0..lastIndex) {
    val first = get(i)
    val second: T? = if (i == lastIndex) null else get(i + 1)
    pairs.add(first to second)
  }
  return pairs
}
