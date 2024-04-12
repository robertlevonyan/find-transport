package robert.findtransport.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.data.service.LocaleService
import robert.findtransport.presentation.navigation.Navigation
import robert.findtransport.presentation.reusables.A2bTheme
import robert.findtransport.presentation.reusables.LocalActivity
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
          Surface(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            Navigation()
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun Preview() {
  val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
  Row(modifier = Modifier
    .fillMaxWidth()
    .background(Color.White)) {
    Image(
      modifier = Modifier.size(50.dp),
      painter = painterResource(id = R.drawable.ic_feedback),
      contentDescription = null,
    )
    Image(
      modifier = Modifier
        .width(screenWidth.dp - 50.dp)
        .height(50.dp),
      painter = painterResource(id = R.drawable.ic_feedback),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      alignment = Alignment.TopEnd,
    )
  }
}