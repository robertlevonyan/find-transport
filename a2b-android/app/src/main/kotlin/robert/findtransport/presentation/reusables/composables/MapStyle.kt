package robert.findtransport.presentation.reusables.composables

import androidx.compose.runtime.Composable
import robert.findtransport.BuildConfig
import robert.findtransport.presentation.reusables.theme.isAppInDarkMode

@Composable
fun getMapStyle(): String = if (isAppInDarkMode()) {
  BuildConfig.MAPBOX_STYLE_NIGHT
} else {
  BuildConfig.MAPBOX_STYLE_LIGHT
}
