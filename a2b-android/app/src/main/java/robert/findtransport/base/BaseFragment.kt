package robert.findtransport.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseFragment<ViewModel : BaseViewModel, Binding : ViewBinding> : Fragment() {
  abstract val binding: Binding
  abstract val viewModel: ViewModel

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

  protected inline fun <reified T> collectWithLifecycle(flow: Flow<T>, crossinline collector: suspend (T) -> Unit) {
    lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        flow.collectLatest { collector(it) }
      }
    }
  }

}
