package robert.findtransport.presentation.stop

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.model.enums.OpenStopType
import robert.findtransport.databinding.FragmentStopsPickerBinding
import robert.findtransport.presentation.component.adapter.FoundStopsAdapter
import robert.findtransport.presentation.component.adapter.StopsAdapter
import robert.findtransport.utils.ARG_STOP_TYPE
import robert.findtransport.utils.RESULT_FROM
import robert.findtransport.utils.RESULT_TO
import robert.findtransport.utils.extensions.onWindowInsets
import robert.findtransport.utils.extensions.topMargin
import robert.findtransport.utils.viewbinding.viewBinding

class StopsPickerFragment : BaseFragment<StopsPickerViewModel, FragmentStopsPickerBinding>() {
  override val binding: FragmentStopsPickerBinding by viewBinding(FragmentStopsPickerBinding::inflate)
  override val viewModel: StopsPickerViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    arguments
        ?.takeIf { it.containsKey(ARG_STOP_TYPE) }
        ?.run { OpenStopType.getByIndex(getInt(ARG_STOP_TYPE)) }
        ?.let { openStopType ->
          observe(viewModel.selectedStop) { stop ->
            when (openStopType) {
              OpenStopType.FROM -> setFragmentResult(RESULT_FROM, bundleOf(RESULT_FROM to stop.id))
              OpenStopType.TO -> setFragmentResult(RESULT_TO, bundleOf(RESULT_TO to stop.id))
              OpenStopType.UNDEFINED -> parentFragmentManager.popBackStack()
            }
            parentFragmentManager.popBackStack()
          }
        }
        ?: parentFragmentManager.popBackStack()
  }

  override fun FragmentStopsPickerBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentStopsPickerBinding.initViews() {
    clRoot.setOnClickListener { }
    ivSearch.setOnClickListener { viewModel.toggleSearchMode() }
    etSearch.doAfterTextChanged { viewModel.findStops(it?.toString() ?: return@doAfterTextChanged) }
  }

  override fun StopsPickerViewModel.initObservers() {
    observe(searchMode) { binding.flTitle.visibility = if (it) View.GONE else View.VISIBLE }
    observe(searchMode) { binding.etSearch.visibility = if (it) View.VISIBLE else View.GONE }
    observe(showNoData) { binding.tvNoStops.visibility = if (it) View.VISIBLE else View.GONE }
    observe(autocompleteStops) { binding.rvStops.adapter = FoundStopsAdapter(viewModel).apply { submitList(it) } }
    observe(allStops) {
      binding.rvStops.adapter = StopsAdapter(viewModel).apply { submitData(viewLifecycleOwner.lifecycle, it) }
    }
    observe(searchMode) {
      if (!it) return@observe
      lifecycleScope.launchWhenCreated {
        delay(100)
        binding.etSearch.requestFocus()
        context?.run {
          val imm = getSystemService(this, InputMethodManager::class.java)
          imm?.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance(openStopType: Int) = StopsPickerFragment().apply {
      arguments = bundleOf(ARG_STOP_TYPE to openStopType)
    }
  }
}
