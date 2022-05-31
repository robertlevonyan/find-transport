package robert.findtransport.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.data.model.DataLoading
import robert.findtransport.presentation.compose.navigation.Navigation
import robert.findtransport.presentation.compose.reusables.A2bTheme
import robert.findtransport.presentation.compose.reusables.backgroundColor
import robert.findtransport.utils.extensions.isTablet

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val mainViewModel: MainViewModel by viewModels()

//  private val noDataDialog: MessageDialog? by lazy {
//    MessageDialog.newInstance(
//      this, bundleOf(
//        ARG_MESSAGE_TITLE to R.string.title_oops,
//        ARG_MESSAGE_DESCRIPTION to R.string.message_no_data
//      )
//    ).apply {
//      onYesClick = { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
//      onNoClick = { finishAffinity() }
//    }
//  }

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
      A2bTheme {
        ProvideWindowInsets {
          Surface(modifier = Modifier.background(color = backgroundColor())) {
            Navigation()
          }
        }
      }
    }
  }

  private fun addInitialDataListener() {
    val content: View = findViewById(android.R.id.content)

    content.viewTreeObserver.addOnPreDrawListener(
      object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
          return when (mainViewModel.loaded.value) {
            is DataLoading.Failed, DataLoading.Loaded -> {
              content.viewTreeObserver.removeOnPreDrawListener(this)
              true
            }
            DataLoading.Loading, DataLoading.NotStarted -> false
          }
        }
      }
    )
  }

//  private fun showEmptyDatabaseDialog() {
//    if (!isFinishing) {
//      noDataDialog?.show()
//    }
//  }
//
//  private fun showLoadingErrorDialog() {
//    noDataDialog?.run {
//      message = getString(R.string.message_error_download)
//      show()
//    }
//  }
//
//  private fun showLoadingErrorDiskFullDialog() {
//    noDataDialog?.run {
//      message = getString(R.string.message_error_download_disk_full)
//      show()
//    }
//  }
}
