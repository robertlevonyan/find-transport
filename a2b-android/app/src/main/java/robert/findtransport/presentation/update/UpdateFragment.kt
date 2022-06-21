package robert.findtransport.presentation.update

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentUpdateBinding
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class UpdateFragment : BaseFragment<UpdateViewModel, FragmentUpdateBinding>() {
  override val binding: FragmentUpdateBinding by viewBinding(FragmentUpdateBinding::inflate)
  override val viewModel: UpdateViewModel by viewModels()

  override fun UpdateViewModel.initObservers() {
  }

  companion object {
    fun newInstance() = UpdateFragment()
  }
}
