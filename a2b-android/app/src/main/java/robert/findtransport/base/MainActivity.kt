package robert.findtransport.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.LogWriter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
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
import robert.findtransport.domain.usecase.feedback.FeedbackUseCase
import robert.findtransport.presentation.home.HomeFragment
import robert.findtransport.presentation.intro.IntroFragment
import robert.findtransport.utils.extensions.fitSystemWindows
import robert.findtransport.utils.extensions.isTablet
import robert.findtransport.utils.viewbinding.viewBinding
import java.io.BufferedWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), ChainHolder {
  @Suppress("unused")
  private val binding by viewBinding(ActivityMainBinding::inflate)
  private val navigatorHolder: NavigatorHolder by inject()
  private val feedbackUseCase: FeedbackUseCase by inject()

  override val chain: MutableList<WeakReference<Fragment>> = mutableListOf()

  private val navigator: Navigator = object : AppNavigator(this, R.id.frContainer) {
    override fun setupFragmentTransaction(
      screen: FragmentScreen,
      fragmentTransaction: FragmentTransaction,
      currentFragment: Fragment?,
      nextFragment: Fragment
    ) {
      if (nextFragment is HomeFragment || nextFragment is IntroFragment) {
        if (currentFragment !is IntroFragment) {
          fragmentTransaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
        }
      } else {
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
      }
      super.setupFragmentTransaction(screen, fragmentTransaction, currentFragment, nextFragment)
    }

    override fun applyCommandsSync(commands: Array<out Command>) {
      if (isDestroyed || isFinishing || supportFragmentManager.isDestroyed || supportFragmentManager.isStateSaved) return

      try {
        super.applyCommandsSync(commands)
        supportFragmentManager.executePendingTransactions()
      } catch (e: Exception) {
        e.printStackTrace()
      }
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

//    if (DynamicColors.isDynamicColorAvailable()) {
//      DynamicColors.applyIfAvailable(this)
//    }

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

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      lifecycleScope.launchWhenCreated {
        val printWriter = PrintWriter(StringWriter())
        throwable.printStackTrace(printWriter)

        feedbackUseCase.sendFeedback(
          email = "error@a2b.com",
          subject = "ActivityThread",
          message = """
            Thread name: ${thread.name}
            Error message: ${throwable.message}
            Stacktrace: $printWriter
          """.trimIndent(),
        )
      }
    }

    openApp()
  }

  override fun onResumeFragments() {
    navigatorHolder.setNavigator(navigator)
    super.onResumeFragments()
  }

  override fun onPause() {
    navigatorHolder.removeNavigator()
    super.onPause()
  }

  private fun openApp() {
    navigator.applyCommands(arrayOf<Command>(Replace(splashScreen())))
  }
}
