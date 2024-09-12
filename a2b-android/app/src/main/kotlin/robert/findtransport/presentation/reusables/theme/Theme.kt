package robert.findtransport.presentation.reusables.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
        systemUiController.setSystemBarsColor(color = it.primary)
    }

    MaterialTheme(
        colorScheme = colors,
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

private val DarkColorPalette = darkColorScheme(
    primary = Black,
    primaryContainer = BlackPure,
    onPrimary = WhiteVariant,
    secondary = Accent,
    secondaryContainer = AccentVariant,
    onSecondary = Black,
    background = Black,
    surface = BlackVariant,
    onSurface = WhiteVariant,
)

private val LightColorPalette = lightColorScheme(
    primary = WhitePure,
    primaryContainer = White,
    onPrimary = BlackVariant,
    secondary = Accent,
    secondaryContainer = AccentVariant,
    onSecondary = Black,
    background = White,
    surface = WhiteVariant,
    onSurface = BlackVariant,
)