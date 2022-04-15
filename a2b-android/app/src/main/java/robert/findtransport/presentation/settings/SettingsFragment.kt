package robert.findtransport.presentation.settings

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.model.SettingData
import robert.findtransport.data.service.LocaleService
import robert.findtransport.databinding.FragmentSettingsBinding
import robert.findtransport.presentation.component.adapter.SettingsAdapter
import robert.findtransport.presentation.component.bottomsheet.language.LanguagePickerBottomSheet
import robert.findtransport.presentation.component.bottomsheet.theme.ThemePickerBottomSheet
import robert.findtransport.presentation.component.rv.VerticalSpaceItemDecoration
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>() {
  override val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::inflate)
  override val viewModel: SettingsViewModel by viewModels()

  private var downloadSnackbar: Snackbar? = null
  private val settingsAdapter by lazy {
    SettingsAdapter { settingData ->
      when (settingData.type) {
        SettingData.SettingType.LANGUAGE -> openLanguagePicker()
        SettingData.SettingType.THEME -> openThemePicker()
        SettingData.SettingType.UPDATE_CELLULAR -> viewModel.updateCellular()
        SettingData.SettingType.PUSH -> viewModel.updatePush()
        SettingData.SettingType.CHECK_UPDATE -> viewModel.checkForUpdate()
        SettingData.SettingType.RATE -> rate()
        SettingData.SettingType.VERSION -> return@SettingsAdapter
      }
    }
  }

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.rvSettings.adapter = settingsAdapter

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
    collectWithLifecycle(settingsList) { settings ->
      if (binding.rvSettings.itemDecorationCount == 0) {
        binding.rvSettings.addItemDecoration(VerticalSpaceItemDecoration())
      }
      settingsAdapter.submitList(settings)
    }
    collectWithLifecycle(languageSave) { activity?.fullRecreate() }
    collectWithLifecycle(themeSave) { activity?.fullRecreate() }
    collectWithLifecycle(newVersion) {
      view?.showSnackbar(
        getString(R.string.message_update),
        getString(R.string.label_yes)
      ) { downloadUpdate() }
    }
    collectWithLifecycle(noNewVersion) { view?.showSnackbar(R.string.message_no_update) }
    collectWithLifecycle(downloadStart) { downloadSnackbar = view?.showInfiniteSnackbar(R.string.message_downloading) }
    collectWithLifecycle(downloadDone) { activity?.fullRecreate() }
    collectWithLifecycle(downloadError) { downloadSnackbar?.dismiss() }
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
    val currentActivity = activity ?: return
    val reviewManager = ReviewManagerFactory.create(currentActivity)
    val requestReviewFlow = reviewManager.requestReviewFlow()
    requestReviewFlow.addOnCompleteListener { request ->
      if (request.isSuccessful) {
        val reviewInfo = request.result
        val flow = reviewManager.launchReviewFlow(currentActivity, reviewInfo)
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

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance() = SettingsFragment()
  }
}
