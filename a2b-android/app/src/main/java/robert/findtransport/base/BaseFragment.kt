package robert.findtransport.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import robert.findtransport.R
import robert.findtransport.presentation.feedback.FeedbackFragment
import robert.findtransport.presentation.settings.SettingsFragment
import robert.findtransport.utils.extensions.add
import robert.findtransport.utils.extensions.getColorFromRes
import robert.findtransport.utils.extensions.showToast

abstract class BaseFragment<ViewModel : BaseViewModel, Binding : ViewBinding> : Fragment() {
  abstract val binding: Binding
  abstract val viewModel: ViewModel

  override fun onAttach(context: Context) {
    super.onAttach(context)
    activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        onBackPressed()
      }
    })
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    preload()
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.initInsets()
    super.onViewCreated(view, savedInstanceState)
    initActionBar()
    binding.initViews()
    viewModel.initObservers()
  }

  open fun Binding.initInsets() = Unit

  open fun Binding.initViews() = Unit

  private fun initActionBar() = activity?.takeIf { activity is AppCompatActivity }
      ?.let { fragmentActivity -> (fragmentActivity as AppCompatActivity).initActionBar() }

  open fun AppCompatActivity.initActionBar() = Unit

  open fun preload() = Unit

  open fun ViewModel.initObservers() = Unit

  protected fun <T> observe(liveData: LiveData<T>, action: (T) -> Unit) = view?.run {
    liveData.observe(viewLifecycleOwner, Observer { action(it ?: return@Observer) })
  } ?: Unit

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        android.R.id.home -> onBackPressed().run { true }
        R.id.action_settings -> add(SettingsFragment.newInstance()).run { true }
        R.id.action_feedback -> add(FeedbackFragment.newInstance()).run { true }
        R.id.action_privacy -> openPrivacyPolicy().run { true }
        else -> true
      }

  open fun onBackPressed() = activity?.run {
    if (parentFragmentManager.backStackEntryCount == 0) {
      finish()
    } else {
      supportFragmentManager.popBackStack()
    }
  } ?: Unit

  private fun openPrivacyPolicy() {
    try {
      context?.run {
        val url = "https://www.freeprivacypolicy.com/privacy/view/58828427193536dad1fea738a43a0758"
        CustomTabsIntent.Builder().run {
          setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
              .setToolbarColor(getColorFromRes(R.color.colorPrimary))
              .setSecondaryToolbarColor(getColorFromRes(R.color.colorDarkGray))
              .build()
          )
          build()
        }.launchUrl(this, url.toUri())
      }
    } catch (e:Exception) {
      e.printStackTrace()
      showToast("Google Chrome cannot be found")
    }
  }
}
