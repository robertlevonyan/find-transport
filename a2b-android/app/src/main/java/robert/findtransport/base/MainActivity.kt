package robert.findtransport.base

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.color.DynamicColors
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import robert.findtransport.R
import robert.findtransport.data.model.DataLoading
import robert.findtransport.data.service.LocaleService
import robert.findtransport.databinding.ActivityMainBinding
import robert.findtransport.di.homeScreen
import robert.findtransport.di.introScreen
import robert.findtransport.presentation.component.dialog.MessageDialog
import robert.findtransport.presentation.home.HomeFragment
import robert.findtransport.presentation.intro.IntroFragment
import robert.findtransport.utils.ARG_MESSAGE_DESCRIPTION
import robert.findtransport.utils.ARG_MESSAGE_TITLE
import robert.findtransport.utils.extensions.fitSystemWindows
import robert.findtransport.utils.extensions.isTablet
import robert.findtransport.utils.observeInLifecycle
import robert.findtransport.utils.viewbinding.viewBinding
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ChainHolder {
  @Suppress("unused")
  private val binding by viewBinding(ActivityMainBinding::inflate)
  private val mainViewModel: MainViewModel by viewModels()

  @Inject
  lateinit var navigatorHolder: NavigatorHolder

  override val chain: MutableList<WeakReference<Fragment>> = mutableListOf()

  private val noDataDialog: MessageDialog? by lazy {
    MessageDialog.newInstance(
      this, bundleOf(
        ARG_MESSAGE_TITLE to R.string.title_oops,
        ARG_MESSAGE_DESCRIPTION to R.string.message_no_data
      )
    ).apply {
      onYesClick = { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
      onNoClick = { finishAffinity() }
    }
  }

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

    if (DynamicColors.isDynamicColorAvailable()) {
      DynamicColors.applyIfAvailable(this)
    }

    installSplashScreen().also { splashScreen ->
      splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
        ObjectAnimator.ofFloat(splashScreenViewProvider.view, View.ALPHA, 1f, 0f).apply {
          interpolator = AnticipateInterpolator()
          duration = 500L
          doOnEnd { splashScreenViewProvider.remove() }
        }.start()
      }
    }

    ActivityMainBinding.inflate(layoutInflater).run {
      setContentView(root)
      window.fitSystemWindows()
    }
    addInitialDataListener()

    val appUpdateManager = AppUpdateManagerFactory.create(this)
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
      if (appUpdateInfo?.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
      ) {
        appUpdateManager.startUpdateFlow(appUpdateInfo, this, AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE))
          .addOnSuccessListener { recreate() }
          .addOnFailureListener { openMain() }
      }
    }

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      mainViewModel.sendErrorFeedback(thread, throwable)
    }

    mainViewModel.run {
      observe(theme) { theme -> delegate.localNightMode = theme }
      observe(currentLanguage) { LocaleService(this@MainActivity).changeLocale(it) }
      observe(nextIntro) { openIntro() }
      observe(nextMain) { openMain() }
      observe(emptyDatabase) { showEmptyDatabaseDialog() }
      observe(loadingError) { showLoadingErrorDialog() }
      observe(loadingDiskFull) { showLoadingErrorDiskFullDialog() }
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

  private fun showEmptyDatabaseDialog() {
    noDataDialog?.show()
  }

  private fun showLoadingErrorDialog() {
    noDataDialog?.run {
      message = getString(R.string.message_error_download)
      show()
    }
  }

  private fun showLoadingErrorDiskFullDialog() {
    noDataDialog?.run {
      message = getString(R.string.message_error_download_disk_full)
      show()
    }
  }

  override fun onResume() {
    super.onResume()
    mainViewModel.checkData()
  }

  override fun onResumeFragments() {
    navigatorHolder.setNavigator(navigator)
    super.onResumeFragments()
  }

  override fun onPause() {
    navigatorHolder.removeNavigator()
    super.onPause()
  }

  private inline fun <reified T> observe(flow: Flow<T>, crossinline action: (T) -> Unit) {
    flow.onEach { action(it) }.observeInLifecycle(this)
  }

  private fun openIntro() {
    navigator.applyCommands(arrayOf<Command>(Replace(introScreen())))
  }

  private fun openMain() {
    navigator.applyCommands(arrayOf<Command>(Replace(homeScreen())))
  }
}
