package robert.findtransport.data.service

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import robert.findtransport.R
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.utils.LNG_AM
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU

class ResourcesService(private val context: Context) {
  
  val languageShortNames by lazy {
    arrayOf(
        context.getString(R.string.settings_language_am_short),
        context.getString(R.string.settings_language_en_short),
        context.getString(R.string.settings_language_ru_short)
    )
  }
  
  val languageShortForSettings by lazy {
    arrayOf(LNG_AM, LNG_EN, LNG_RU)
  }
  
  val languageNames by lazy {
    arrayOf(
        context.getString(R.string.settings_language_am),
        context.getString(R.string.settings_language_en),
        context.getString(R.string.settings_language_ru)
    )
  }
  
  val themeShortNames by lazy {
    arrayOf(
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
        AppCompatDelegate.MODE_NIGHT_NO,
        AppCompatDelegate.MODE_NIGHT_YES
    )
  }
  
  val themeNames by lazy {
    arrayOf(
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
        AppCompatDelegate.MODE_NIGHT_NO,
        AppCompatDelegate.MODE_NIGHT_YES
    )
  }
  
  fun getExceptionMessage(type: ExceptionType) = when (type) {
    ExceptionType.EMPTY_EMAIL -> R.string.error_empty_email
    ExceptionType.WRONG_EMAIL -> R.string.error_email
    ExceptionType.ERROR_SUBJECT -> R.string.error_subject
    ExceptionType.EMPTY_MESSAGE -> R.string.error_message
    ExceptionType.SHORT_MESSAGE -> R.string.error_message_short
    else -> -1
  }
  
}
