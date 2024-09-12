package robert.findtransport.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.data.service.LocaleService
import robert.findtransport.presentation.navigation.Navigation
import robert.findtransport.presentation.reusables.activity.LocalActivity
import robert.findtransport.presentation.reusables.theme.A2bTheme
import robert.findtransport.utils.extensions.requestedOrientation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation()
        installSplashScreen()
        setContent {
            CompositionLocalProvider(LocalActivity provides this) {
                val theme by mainViewModel.theme.collectAsState()
                val currentLanguage by mainViewModel.currentLanguage.collectAsState()
                LocaleService(this).changeLocale(currentLanguage)
                A2bTheme(theme) {
                    enableEdgeToEdge()
                    Surface(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                        Navigation()
                    }
                }
            }
        }
    }
}
