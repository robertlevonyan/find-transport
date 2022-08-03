package robert.findtransport.presentation.compose.reusables

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun A2bTheme(
  theme: Int,
  content: @Composable () -> Unit
) {
  AppCompatDelegate.setDefaultNightMode(theme)
  val systemUiController = rememberSystemUiController()

  val colors = when (theme) {
    AppCompatDelegate.MODE_NIGHT_NO -> LightColorPalette
    AppCompatDelegate.MODE_NIGHT_YES -> DarkColorPalette
    else -> if (isSystemInDarkTheme()) {
      DarkColorPalette
    } else {
      LightColorPalette
    }
  }.also {
    systemUiController.setSystemBarsColor(color = it.primaryVariant)
  }

  MaterialTheme(
    colors = colors,
    typography = Typography,
    shapes = Shapes,
    content = content
  )
}

@Composable
fun isAppInDarkMode(): Boolean = when (AppCompatDelegate.getDefaultNightMode()) {
  AppCompatDelegate.MODE_NIGHT_NO -> false
  AppCompatDelegate.MODE_NIGHT_YES -> true
  else -> isSystemInDarkTheme()
}

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
  primary = Black,
  primaryVariant = BlackPure,
  onPrimary = WhiteVariant,
  secondary = Accent,
  secondaryVariant = AccentVariant,
  onSecondary = Black,
  background = Black,
  surface = BlackVariant,
  onSurface = WhiteVariant,
)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
  primary = WhitePure,
  primaryVariant = White,
  onPrimary = BlackVariant,
  secondary = Accent,
  secondaryVariant = AccentVariant,
  onSecondary = Black,
  background = White,
  surface = WhiteVariant,
  onSurface = BlackVariant,
)