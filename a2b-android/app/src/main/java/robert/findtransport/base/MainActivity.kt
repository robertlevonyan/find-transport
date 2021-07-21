package robert.findtransport.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import robert.findtransport.R
import robert.findtransport.databinding.ActivityMainBinding
import robert.findtransport.presentation.splash.SplashFragment
import robert.findtransport.utils.extensions.fitSystemWindows
import robert.findtransport.utils.extensions.isTablet
import robert.findtransport.utils.viewbinding.viewBinding

class MainActivity : AppCompatActivity() {
  @Suppress("unused")
  private val binding by viewBinding(ActivityMainBinding::inflate)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
      if (isTablet()) {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
      } else {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
      }
    } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    ActivityMainBinding.inflate(layoutInflater).run {
      setContentView(root)
      window.fitSystemWindows()
    }

    val appUpdateManager = AppUpdateManagerFactory.create(this)
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
      if (appUpdateInfo?.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
      ) {
        appUpdateManager.startUpdateFlow(appUpdateInfo, this, AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE))
          .addOnSuccessListener { recreate() }
          .addOnFailureListener { openApp() }
      }
    }

    openApp()
  }

  override fun onResume() {
    super.onResume()
    requestedOrientation = if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
      if (isTablet()) {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
      } else {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
      }
    } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  }

  override fun onPause() {
    super.onPause()
    requestedOrientation = if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
      ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  }

  private fun openApp() {
    supportFragmentManager.beginTransaction()
      .replace(R.id.frContainer, SplashFragment.newInstance())
      .commitNowAllowingStateLoss()
  }
}
