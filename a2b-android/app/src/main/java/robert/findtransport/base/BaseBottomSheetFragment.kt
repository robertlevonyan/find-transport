package robert.findtransport.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import robert.findtransport.R

abstract class BaseBottomSheetFragment<ViewModel : BaseViewModel, Binding : ViewBinding> : BottomSheetDialogFragment() {
  abstract val binding: Binding
  abstract val viewModel: ViewModel

  override fun getTheme(): Int = R.style.BottomSheetTheme_Dark_Dialog

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = binding.root

  protected fun <T> observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer { action(it ?: return@Observer) })
  }
}
