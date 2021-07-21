package robert.findtransport.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SettingData(
    val type: SettingType,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    @StringRes val detail: Int,
    var additionalInfo: Any? = null,
    val endViewType: EndViewType = EndViewType.NONE
) {
  enum class SettingType {
    LANGUAGE, THEME, UPDATE_CELLULAR, PUSH, CHECK_UPDATE, RATE, VERSION
  }
  
  enum class EndViewType {
    NONE, IMAGE, SWITCH, PROGRESS
  }
}