package robert.findtransport.utils.extensions

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import robert.findtransport.BuildConfig
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.sqrt

fun String.isEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

@SuppressLint("LogNotTimber")
suspend fun <R> makeApiCall(call: suspend () -> R) = try {
    Result.Success(call())
} catch (e: Exception) {
    Log.e("A2B", "ERROR", e)
    Result.Error(A2bException(ExceptionType.API, -1, e))
}

fun getHeader(): String {
    val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())
    return md5(BuildConfig.KEY_PREFIX, date)
}

fun isTablet(): Boolean {
    val metrics = Resources.getSystem().displayMetrics

    val yInches = metrics.heightPixels / metrics.ydpi
    val xInches = metrics.widthPixels / metrics.xdpi
    val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
    return diagonalInches >= 6.8
}

fun Date.format(): String = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(this)

context(BaseViewModel)
fun <T> Flow<T>.asStateFlow(
    default: T,
    started: SharingStarted = SharingStarted.Lazily
): StateFlow<T> = stateIn(viewModelScope, started = started, initialValue = default)
