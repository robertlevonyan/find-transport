package robert.findtransport.presentation.passing

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentPassingRoutesBinding
import robert.findtransport.presentation.component.adapter.TransportsListAdapter
import robert.findtransport.utils.ARG_STOP
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class PassingRoutesFragment : BaseFragment<PassingRoutesViewModel, FragmentPassingRoutesBinding>() {
  override val binding: FragmentPassingRoutesBinding by viewBinding(FragmentPassingRoutesBinding::inflate)
  override val viewModel: PassingRoutesViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    arguments
      ?.takeIf { it.containsKey(ARG_STOP) }
      ?.run { getInt(ARG_STOP) }
      ?.let {
        viewModel.getStop(it)
        viewModel.getTransports(it)
      }
      ?: router.exit()
  }

  override fun FragmentPassingRoutesBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    rvTransportsList.onWindowInsets { v, windowInsets ->
      v.bottomPadding = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin) * 2
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun PassingRoutesViewModel.initObservers() {
    observe(stopTransports) { transports ->
      val locale = viewModel.locale.value
      binding.rvTransportsList.adapter = TransportsListAdapter().apply {
        currentLocale = locale
        submitList(transports)
      }
    }
    observe(stopReceived) { stop ->
      val locale = viewModel.locale.value
      binding.tvSelectedStop.setSelectedStopName(stop, locale)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_details, menu.apply { clear() })
  }

  companion object {
    fun newInstance(stopId: Int) = PassingRoutesFragment().apply {
      arguments = bundleOf(ARG_STOP to stopId)
    }
  }
}
