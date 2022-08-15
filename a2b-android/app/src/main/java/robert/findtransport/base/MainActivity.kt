package robert.findtransport.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.data.service.LocaleService
import robert.findtransport.presentation.compose.navigation.Navigation
import robert.findtransport.presentation.compose.reusables.A2bTheme
import robert.findtransport.utils.extensions.isTablet

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val mainViewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
      if (isTablet()) {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
      } else {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
      }
    } else {
      ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    installSplashScreen()
    setContent {
      val theme by mainViewModel.theme.collectAsState()
      val currentLanguage by mainViewModel.currentLanguage.collectAsState()
      LocaleService(this).changeLocale(currentLanguage)
      A2bTheme(theme) {
        ProvideWindowInsets {
          Surface(modifier = Modifier.background(color = MaterialTheme.colors.background)) {
            Navigation()
          }
        }
      }
    }
  }
}
