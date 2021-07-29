package robert.findtransport.presentation.update

import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentUpdateBinding
import robert.findtransport.di.homeScreen
import robert.findtransport.utils.viewbinding.viewBinding

class UpdateFragment : BaseFragment<UpdateViewModel, FragmentUpdateBinding>() {
  override val binding: FragmentUpdateBinding by viewBinding(FragmentUpdateBinding::inflate)
  override val viewModel: UpdateViewModel by viewModel()

  override fun UpdateViewModel.initObservers() {
    observe(onComplete) { router.navigateTo(homeScreen()) }
  }

  companion object {
    fun newInstance() = UpdateFragment()
  }
}
