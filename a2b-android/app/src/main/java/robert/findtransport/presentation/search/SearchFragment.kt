package robert.findtransport.presentation.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResultListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.model.MultiRouteCase
import robert.findtransport.data.model.MultiType
import robert.findtransport.databinding.FragmentSearchBinding
import robert.findtransport.presentation.component.adapter.MultiRouteAdapter
import robert.findtransport.presentation.component.adapter.TransportsListAdapter
import robert.findtransport.presentation.component.dialog.ArrivedDialog
import robert.findtransport.presentation.detail.DetailFragment
import robert.findtransport.presentation.map.SearchMapFragment
import robert.findtransport.presentation.track.TrackRouteFragment
import robert.findtransport.utils.*
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {
  override val binding: FragmentSearchBinding by viewBinding(FragmentSearchBinding::inflate)
  override val viewModel: SearchViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    arguments?.takeIf { it.containsKey(ARG_FROM_ID) && it.containsKey(ARG_TO_ID) }?.run {
      viewModel.getData(getInt(ARG_FROM_ID), getInt(ARG_TO_ID), getBoolean(ARG_ADD_TO_HISTORY))
    }

    setFragmentResultListener(RESULT_ARRIVED) { _, _ ->
      ArrivedDialog.newInstance().show(parentFragmentManager, ArrivedDialog::class.java.simpleName)
    }
  }

  override fun FragmentSearchBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    rvTransportsList.onWindowInsets { v, windowInsets ->
      v.topPadding = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
      v.bottomPadding = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
    fabTrackRoute.onWindowInsets { v, windowInsets ->
      v.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentSearchBinding.initViews() {
    fabShowMap.setOnClickListener {
      val fromId = viewModel.fromStop.value?.id ?: return@setOnClickListener
      val toId = viewModel.toStop.value?.id ?: return@setOnClickListener

      addWithSlide(
        SearchMapFragment.newInstance(
          bundleOf(
            ARG_FROM_ID to fromId,
            ARG_TO_ID to toId,
          )
        )
      )
    }

    fabTrackRoute.setOnClickListener { }
  }

  override fun SearchViewModel.initObservers() {
    observe(searchEmpty) { showToast("NOTHING") }
    observe(selectedTransport) { addWithSlide(DetailFragment.newInstance(it.id, false)) }
    observe(emptyStop) { stopNotFound() }
    observe(loading) { binding.progressLoading.visibility = if (it) View.VISIBLE else View.GONE }
    observe(searchTransports) { transports ->
      val locale = viewModel.locale.value ?: return@observe
      val fromId = viewModel.fromStop.value?.id ?: return@observe
      val toId = viewModel.toStop.value?.id ?: return@observe

      binding.rvTransportsList.adapter = TransportsListAdapter(viewModel::openTransport).apply {
        currentLocale = locale
        setOnTransportTrackClickListener { transport ->
          add(
            TrackRouteFragment.newInstance(
              bundleOf(
                ARG_TRANSPORT_ID to transport.id,
                ARG_FROM_ID to fromId,
                ARG_TO_ID to toId,
              )
            )
          )
        }
        submitList(transports)
      }
    }
    observe(searchMultiTransports) { multiRoots ->
      val locale = viewModel.locale.value ?: return@observe

      binding.rvTransportsList.adapter = MultiRouteAdapter(locale, viewModel).apply {
        var fromId = 0
        var toId = 0
        var selectedTransportPosition = -1
        setOnTransportTrackClickListener { transport ->
          multiRoots.groupBy { it.case }.entries.forEach { entry ->
            when (entry.key) {
              MultiRouteCase.SINGLE_FROM -> {
                entry.value.forEach { multiRoot ->
                  when (multiRoot.type) {
                    MultiType.TRANSPORT_TITLE -> fromId = multiRoot.stop?.id ?: 0
                    MultiType.INTERCHANGE_TO -> toId = multiRoot.stop?.id ?: 0
                    else -> Unit
                  }
                }
              }
              MultiRouteCase.SINGLE_TO -> {
                entry.value.forEach { multiRoot ->
                  when (multiRoot.type) {
                    MultiType.INTERCHANGE_FROM -> {
                      fromId = multiRoot.stop?.id ?: 0
                      toId = viewModel.toStop.value?.id ?: 0
                    }
                    else -> Unit
                  }
                }
              }
              MultiRouteCase.FROM_TO -> {
                if (selectedTransportPosition == -1) {
                  entry.value.forEachIndexed { index, multiRoute ->
                    if (multiRoute.transport?.id == transport.id) {
                      selectedTransportPosition = index
                    }
                  }
                }
                val interchangePosition = entry.value.indexOfFirst { it.type == MultiType.INTERCHANGE_TO }
                entry.value.forEach { multiRoute ->
                  when (multiRoute.type) {
                    MultiType.TRANSPORT_TITLE -> {
                      // start or nothing
                      if (selectedTransportPosition < interchangePosition) {
                        fromId = multiRoute.stop?.id ?: 0
                      }
                    }
                    MultiType.INTERCHANGE_TO -> {
                      // end or start
                      if (selectedTransportPosition < interchangePosition) {
                        if (toId == 0) {
                          toId = multiRoute.stop?.id ?: 0
                        }
                      } else {
                        if (fromId != 0 && toId == 0) {
                          toId = multiRoute.stop?.id ?: 0
                        }
                        if (fromId == 0) {
                          fromId = multiRoute.stop?.id ?: 0
                        }
                      }
                    }
                    else -> Unit
                  }
                }
              }
            }
          }
          add(
            TrackRouteFragment.newInstance(
              bundleOf(
                ARG_TRANSPORT_ID to transport.id,
                ARG_FROM_ID to fromId,
                ARG_TO_ID to toId,
              )
            )
          )
        }
        submitList(multiRoots)
      }
    }
    observe(fromStop) { stop ->
      val locale = viewModel.locale.value ?: return@observe
      binding.tvFromLabel.setStopName(stop, locale)
    }
    observe(toStop) { stop ->
      val locale = viewModel.locale.value ?: return@observe
      binding.tvToLabel.setStopName(stop, locale)
    }
  }

  private fun stopNotFound() {
    showToast(getString(R.string.error_stop_not_found))
    parentFragmentManager.popBackStack()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance(args: Bundle) = SearchFragment().apply { arguments = args }
  }
}
