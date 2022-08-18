package robert.findtransport.presentation.component.bottomsheet.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.base.BaseBottomSheetFragment
import robert.findtransport.data.model.Stop
import robert.findtransport.databinding.BottomSheetStopOptionsBinding
import robert.findtransport.utils.ARG_STOP
import robert.findtransport.utils.extensions.setStopName
import robert.findtransport.utils.extensions.showToast
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class StopOptionsBottomSheet : BaseBottomSheetFragment<StopOptionsViewModel, BottomSheetStopOptionsBinding>() {
  override val binding: BottomSheetStopOptionsBinding by viewBinding(BottomSheetStopOptionsBinding::inflate)
  override val viewModel: StopOptionsViewModel by viewModels()

  var onFromSelected: (Stop) -> Unit = {}
  var onToSelected: (Stop) -> Unit = {}
  var onShowTransports: (Stop) -> Unit = {}

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    arguments?.takeIf { it.containsKey(ARG_STOP) }?.let { args ->
      val id = args.getInt(ARG_STOP)
      viewModel.getCurrentStop(id)
    }

    viewModel.run {
      collectWithLifecycle(fromStop) { stop ->
        onFromSelected(stop)
        dismiss()
      }
      collectWithLifecycle(toStop) { stop ->
        onToSelected(stop)
        dismiss()
      }
      collectWithLifecycle(passingTransports) { stop ->
        onShowTransports(stop)
        dismiss()
      }
      collectWithLifecycle(emptyStop) {
        showToast(getString(R.string.error_stop_not_found))
        dismiss()
      }
      collectWithLifecycle(currentStop) { stop ->
        val locale = viewModel.locale.value ?: return@collectWithLifecycle
        binding.tvTitle.setStopName(stop, locale)
      }
    }

    binding.run {
      llSetFrom.setOnClickListener { viewModel.setFrom() }
      llSetTo.setOnClickListener { viewModel.setTo() }
      llRoute.setOnClickListener { viewModel.showTransports() }
    }
  }

  companion object {
    fun newInstance(args: Bundle) = StopOptionsBottomSheet().apply { arguments = args }
  }
}
