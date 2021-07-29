package robert.findtransport.presentation.settings

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.service.LocaleService
import robert.findtransport.databinding.FragmentSettingsBinding
import robert.findtransport.presentation.component.adapter.SettingsAdapter
import robert.findtransport.presentation.component.bottomsheet.language.LanguagePickerBottomSheet
import robert.findtransport.presentation.component.bottomsheet.theme.ThemePickerBottomSheet
import robert.findtransport.presentation.component.rv.VerticalSpaceItemDecoration
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>() {
  override val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::inflate)
  override val viewModel: SettingsViewModel by viewModel()

  private var downloadSnackbar: Snackbar? = null

  override fun FragmentSettingsBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    ivInnfinity.onWindowInsets { v, windowInsets ->
      v.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.margin_7)
    }
    rvSettings.onWindowInsets { v, windowInsets ->
      v.bottomPadding = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentSettingsBinding.initViews() {
    val horizontalPadding = if (isTablet()) getDimenInt(R.dimen.activity_horizontal_margin_big) else 0
    rvSettings.setPaddingRelative(
        horizontalPadding,
        getDimenInt(R.dimen.fab_margin),
        horizontalPadding,
        0,
    )
  }

  override fun SettingsViewModel.initObservers() {
    observe(settingsList) { settings ->
      if (binding.rvSettings.itemDecorationCount == 0) {
        binding.rvSettings.addItemDecoration(VerticalSpaceItemDecoration())
      }
      binding.rvSettings.adapter = SettingsAdapter(viewModel).apply { submitList(settings) }
    }
    observe(languagePickerEvent) { openLanguagePicker() }
    observe(themePickerEvent) { openThemePicker() }
    observe(languageSave) { activity?.fullRecreate() }
    observe(themeSave) { activity?.fullRecreate() }
    observe(newVersion) { view?.showSnackbar(getString(R.string.message_update), getString(R.string.label_yes)) { downloadUpdate() } }
    observe(noNewVersion) { view?.showSnackbar(R.string.message_no_update) }
    observe(downloadStart) { downloadSnackbar = view?.showInfiniteSnackbar(R.string.message_downloading) }
    observe(downloadDone) { activity?.fullRecreate() }
    observe(downloadError) { downloadSnackbar?.dismiss() }
    observe(openRate) { rate() }
  }

  private fun openLanguagePicker() {
    LanguagePickerBottomSheet().apply {
      onLanguageSelected = { languageData ->
        this@SettingsFragment.viewModel.changeLanguage(languageData)
        activity?.run { LocaleService(this).changeLocale(languageData.languageShortSetting) }
      }
    }.show(parentFragmentManager, LanguagePickerBottomSheet::class.java.simpleName)
  }

  private fun openThemePicker() {
    ThemePickerBottomSheet().apply {
      onThemeSelected = { themeData ->
        this@SettingsFragment.viewModel.changeTheme(themeData)
      }
    }.show(parentFragmentManager, ThemePickerBottomSheet::class.java.simpleName)
  }

  private fun rate() {
    activity?.run {
      val reviewManager = ReviewManagerFactory.create(this)
      val requestReviewFlow = reviewManager.requestReviewFlow()
      requestReviewFlow.addOnCompleteListener { request ->
        if (request.isSuccessful) {
          val reviewInfo = request.result
          val flow = reviewManager.launchReviewFlow(this, reviewInfo)
          flow.addOnCompleteListener {
            if (it.isSuccessful) {
              Log.d("Rate: ", request.result.toString())
            } else {
              Log.e("Error: ", it.exception.toString())
            }
          }
        } else {
          Log.e("Error: ", request.exception.toString())
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance() = SettingsFragment()
  }
}
