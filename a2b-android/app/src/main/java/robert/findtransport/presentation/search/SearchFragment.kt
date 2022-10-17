package robert.findtransport.presentation.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.model.MultiRouteCase
import robert.findtransport.data.model.MultiType
import robert.findtransport.databinding.FragmentSearchBinding
import robert.findtransport.di.detailsScreen
import robert.findtransport.di.mapSearchScreen
import robert.findtransport.di.trackRouteScreen
import robert.findtransport.presentation.component.adapter.MultiRouteAdapter
import robert.findtransport.presentation.component.adapter.TransportsListAdapter
import robert.findtransport.presentation.component.dialog.ArrivedDialog
import robert.findtransport.utils.*
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {
  override val binding: FragmentSearchBinding by viewBinding(FragmentSearchBinding::inflate)
  override val viewModel: SearchViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val from = arguments?.getInt(ARG_FROM_ID) ?: return
    val to = arguments?.getInt(ARG_TO_ID) ?: return
    val fromHistory = arguments?.getBoolean(ARG_ADD_TO_HISTORY) ?: return

    viewModel.getData(from, to, fromHistory)

    setFragmentResultListener(RESULT_ARRIVED) { _, _ ->
      if (activity?.isFinishing != true) {
        ArrivedDialog.newInstance().show(parentFragmentManager, ArrivedDialog::class.java.simpleName)
      }
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
  }

  override fun FragmentSearchBinding.initViews() {
    fabShowMap.setOnClickListener {
      lifecycleScope.launch {
        val fromId = viewModel.fromStop.firstOrNull()?.id ?: 0
        val toId = viewModel.toStop.firstOrNull()?.id ?: 0

        router.navigateTo(
          mapSearchScreen(
            bundleOf(
              ARG_FROM_ID to fromId,
              ARG_TO_ID to toId,
            )
          )
        )
      }
    }

    fabTrackRoute.setOnClickListener { }
  }

  override fun SearchViewModel.initObservers() {
    collectWithLifecycle(searchEmpty) { showToast("NOTHING") }
    collectWithLifecycle(emptyStop) { stopNotFound() }
    collectWithLifecycle(loading) { binding.progressLoading.visibility = if (it) View.VISIBLE else View.GONE }
    collectWithLifecycle(searchTransports) { transports ->
      val locale = viewModel.locale.value
      val fromId = viewModel.fromStop.value.id
      val toId = viewModel.toStop.value.id

      binding.rvTransportsList.adapter = TransportsListAdapter { transport ->
        router.navigateTo(detailsScreen(transport.id, false))
      }.apply {
        currentLocale = locale
        submitList(transports)
        setOnTransportTrackClickListener { transport ->
          router.navigateTo(
            trackRouteScreen(
              bundleOf(
                ARG_TRANSPORT_ID to transport.id,
                ARG_FROM_ID to fromId,
                ARG_TO_ID to toId,
              )
            )
          )
        }
      }
    }
    collectWithLifecycle(searchMultiTransports) { transports ->
      val locale = viewModel.locale.value
      val toStop = viewModel.toStop.value
      var fromId = 0
      var toId = 0
      var selectedTransportPosition = -1

      binding.rvTransportsList.adapter = MultiRouteAdapter { transport ->
        router.navigateTo(detailsScreen(transport.id, false))
      }.apply {
        currentLocale = locale
        submitList(transports)
        setOnTransportTrackClickListener { transport ->
          transports.groupBy { it.case }.entries.forEach { entry ->
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
                      toId = toStop.id
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

          router.navigateTo(
            trackRouteScreen(
              bundleOf(
                ARG_TRANSPORT_ID to transport.id,
                ARG_FROM_ID to fromId,
                ARG_TO_ID to toId,
              )
            )
          )
        }
      }
    }
    collectWithLifecycle(fromStop) { stop ->
      val locale = viewModel.locale.value
      binding.tvFromLabel.setStopName(stop, locale)
    }
    collectWithLifecycle(toStop) { stop ->
      val locale = viewModel.locale.value
      binding.tvToLabel.setStopName(stop, locale)
    }
  }

  private fun stopNotFound() {
    showToast(getString(R.string.error_stop_not_found))
    router.exit()
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance(args: Bundle) = SearchFragment().apply { arguments = args }
  }
}
