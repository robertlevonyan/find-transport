package robert.findtransport.presentation.intro

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.service.LocaleService
import robert.findtransport.databinding.FragmentIntroBinding
import robert.findtransport.di.homeScreen
import robert.findtransport.presentation.compose.screens.intro.IntroViewModel
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class IntroFragment : BaseFragment<IntroViewModel, FragmentIntroBinding>() {
  override val binding: FragmentIntroBinding by viewBinding(FragmentIntroBinding::inflate)
  override val viewModel: IntroViewModel by viewModels()

  override fun FragmentIntroBinding.initViews() {
    btnArm.setOnClickListener { viewModel.setLanguage(0) }
    btnEng.setOnClickListener { viewModel.setLanguage(1) }
    btnRus.setOnClickListener { viewModel.setLanguage(2) }
    fabNext.setOnClickListener { viewModel.setIntroPassed() }
  }

  override fun IntroViewModel.initObservers() = viewModel.run {
    collectWithLifecycle(languageChanged) { language ->
      activity?.run {
        LocaleService(this).changeLocale(language)
//        router.replaceScreen(introScreen())
      }
    }
//    collectWithLifecycle(pickerArmValue) {
//      if (it) {
//        binding.pkLanguage.check(R.id.btn_arm)
//      }
//    }
//    collectWithLifecycle(pickerEngValue) {
//      if (it) {
//        binding.pkLanguage.check(R.id.btn_eng)
//      }
//    }
//    collectWithLifecycle(pickerRusValue) {
//      if (it) {
//        binding.pkLanguage.check(R.id.btn_rus)
//      }
//    }
    collectWithLifecycle(introPassed) { router.replaceScreen(homeScreen()) }
  }

  companion object {
    fun newInstance() = IntroFragment()
  }
}
