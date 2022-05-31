package robert.findtransport.presentation.compose.reusables

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun A2bTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable() () -> Unit
) {
  val colors = if (darkTheme) {
    DarkColorPalette
  } else {
    LightColorPalette
  }

  MaterialTheme(
    colors = colors,
    typography = Typography,
    shapes = Shapes,
    content = content
  )
}

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
  primary = Black,
  primaryVariant = BlackPure,
  onPrimary = White,
  secondary = Accent,
  secondaryVariant = AccentVariant,
  onSecondary = Black,
  background = Black,
  surface = BlackVariant,
  onSurface = White,
)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
  primary = WhitePure,
  primaryVariant = White,
  onPrimary = BlackPure,
  secondary = Accent,
  secondaryVariant = AccentVariant,
  onSecondary = Black,
  background = White,
  surface = WhiteVariant,
  onSurface = Black,
)