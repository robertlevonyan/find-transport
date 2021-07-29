package robert.findtransport.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.koin.android.ext.android.inject
import robert.findtransport.R
import robert.findtransport.databinding.ActivityMainBinding
import robert.findtransport.di.splashScreen
import robert.findtransport.presentation.home.HomeFragment
import robert.findtransport.presentation.intro.IntroFragment
import robert.findtransport.utils.extensions.fitSystemWindows
import robert.findtransport.utils.extensions.isTablet
import robert.findtransport.utils.viewbinding.viewBinding
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), ChainHolder {
  @Suppress("unused")
  private val binding by viewBinding(ActivityMainBinding::inflate)
  private val navigatorHolder: NavigatorHolder by inject()

  override val chain: MutableList<WeakReference<Fragment>> = mutableListOf()

  private val navigator: Navigator = object : AppNavigator(this, R.id.frContainer) {
    override fun setupFragmentTransaction(
      screen: FragmentScreen,
      fragmentTransaction: FragmentTransaction,
      currentFragment: Fragment?,
      nextFragment: Fragment
    ) {
      if (nextFragment is HomeFragment || nextFragment is IntroFragment) {
        fragmentTransaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
      } else {
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
      }
      super.setupFragmentTransaction(screen, fragmentTransaction, currentFragment, nextFragment)
    }

    override fun applyCommandsSync(commands: Array<out Command>) {
      super.applyCommandsSync(commands)
      supportFragmentManager.executePendingTransactions()
    }
  }

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

  override fun onResumeFragments() {
    super.onResumeFragments()
    navigatorHolder.setNavigator(navigator)
  }

  override fun onPause() {
    navigatorHolder.removeNavigator()
    super.onPause()
    requestedOrientation = if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
      ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  }

  private fun openApp() {
    navigator.applyCommands(arrayOf<Command>(Replace(splashScreen())))
  }
}
