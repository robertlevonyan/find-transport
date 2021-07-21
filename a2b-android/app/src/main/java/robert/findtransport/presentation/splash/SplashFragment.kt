package robert.findtransport.presentation.splash

import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.base.MainActivity
import robert.findtransport.data.service.LocaleService
import robert.findtransport.databinding.FragmentSplashBinding
import robert.findtransport.presentation.component.dialog.MessageDialog
import robert.findtransport.presentation.home.HomeFragment
import robert.findtransport.presentation.intro.IntroFragment
import robert.findtransport.utils.ARG_MESSAGE_DESCRIPTION
import robert.findtransport.utils.ARG_MESSAGE_TITLE
import robert.findtransport.utils.extensions.bottomMargin
import robert.findtransport.utils.extensions.getDimenInt
import robert.findtransport.utils.extensions.onWindowInsets
import robert.findtransport.utils.extensions.replaceWithAlpha
import robert.findtransport.utils.viewbinding.viewBinding

class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>() {
  override val binding: FragmentSplashBinding by viewBinding(FragmentSplashBinding::inflate)
  override val viewModel: SplashViewModel by viewModel()

  private val noDataDialog: MessageDialog? by lazy {
    context?.run {
      MessageDialog.newInstance(
        this, bundleOf(
          ARG_MESSAGE_TITLE to R.string.title_oops,
          ARG_MESSAGE_DESCRIPTION to R.string.message_no_data
        )
      ).apply {
        onYesClick = { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
        onNoClick = { activity?.finishAffinity() }
      }
    }
  }

  override fun FragmentSplashBinding.initInsets() {
    ivInnfinity.onWindowInsets { v, windowInsets ->
      v.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.margin_7)
    }
  }

  override fun SplashViewModel.initObservers() {
    observe(themeLiveData) { theme ->
      activity?.takeIf { it is MainActivity }
        ?.let { it as MainActivity }
        ?.run { delegate.localNightMode = theme }
    }
    observe(currentLanguage) { activity?.run { LocaleService(this).changeLocale(it) } }
    observe(nextIntro) { replaceWithAlpha(IntroFragment.newInstance()) }
    observe(nextMain) { replaceWithAlpha(HomeFragment.newInstance()) }
    observe(loadStart) { startLoadingAnimation() }
    observe(loaded) { startLaunchAnimation() }
    observe(emptyDatabase) { showEmptyDatabaseDialog() }
    observe(loadingError) { showLoadingErrorDialog() }
    observe(loadingDiskFull) { showLoadingErrorDiskFullDialog() }
  }

  private fun startLoadingAnimation() {
    context?.run {
      val icon = AnimatedVectorDrawableCompat.create(this, R.drawable.anim_splash_loading)
      binding.ivLogo.setImageDrawable(icon)
      icon?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
          super.onAnimationEnd(drawable)
          icon.start()
        }
      })
      icon?.start()
    }
  }

  private fun startLaunchAnimation() {
    context?.run {
      val icon = AnimatedVectorDrawableCompat.create(this, R.drawable.anim_splash_loaded)
      binding.ivLogo.setImageDrawable(icon)
      icon?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
          super.onAnimationEnd(drawable)
          viewModel.onNext()
        }
      })
      icon?.start()
    }
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
    noDataDialog?.dismiss()
    viewModel.checkData()
  }

  companion object {
    fun newInstance() = SplashFragment()
  }
}
