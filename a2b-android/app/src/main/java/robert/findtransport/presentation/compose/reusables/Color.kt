package robert.findtransport.presentation.compose.reusables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Accent = Color(0xFFE7A942)
val AccentVariant = Color(0xFFFFEDD1)

val Black = Color(0xFF1A1A1A)
val BlackVariant = Color(0xFF3A3A3A)
val BlackPure = Color(0xFF000000)
val BlackTransparent = Color(0x4D1A1A1A)

val White = Color(0xFFF1F1F1)
val WhiteVariant = Color(0xFFE4E4E4)
val WhitePure = Color(0xFFFFFFFF)

@Composable
fun backgroundColor() = if (isSystemInDarkTheme()) Black else White

@Composable
fun backgroundColorVariant() = if (isSystemInDarkTheme()) BlackVariant else WhiteVariant

@Composable
fun searchInputBackgroundColor() = if (isSystemInDarkTheme())
  backgroundColor().copy(alpha = 0.05f) else White.copy(alpha = 0.1f)

@Composable
fun backgroundColorVariantInvert() = if (isSystemInDarkTheme()) WhiteVariant else BlackVariant

@Composable
fun backgroundColorVariantInvertTransparent() = backgroundColorVariantInvert().copy(alpha = 0.5f)