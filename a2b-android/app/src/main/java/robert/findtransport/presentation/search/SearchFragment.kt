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
import kotlinx.coroutines.flow.combineTransform
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
    arguments?.takeIf { it.containsKey(ARG_FROM_ID) && it.containsKey(ARG_TO_ID) }?.run {
      viewModel.getData(getInt(ARG_FROM_ID), getInt(ARG_TO_ID), getBoolean(ARG_ADD_TO_HISTORY))
    }

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
    setHasOptionsMenu(true)
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
    observe(searchEmpty) { showToast("NOTHING") }
    observe(selectedTransport) { router.navigateTo(detailsScreen(it.id, false)) }
    observe(emptyStop) { stopNotFound() }
    observe(loading) { binding.progressLoading.visibility = if (it) View.VISIBLE else View.GONE }
    observe(
      searchTransports
        .combineTransform(fromStop) { transports, fromStop -> emit(transports to fromStop) }
        .combineTransform(toStop) { pair, toStop -> emit(Triple(pair.first, pair.second, toStop)) }
    ) {
      val locale = viewModel.locale.value
      val fromId = it.second.id
      val toId = it.third.id
      if (it.first.isEmpty()) return@observe

      binding.rvTransportsList.adapter = TransportsListAdapter(viewModel::openTransport).apply {
        currentLocale = locale
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
        submitList(it.first)
      }
    }
    observe(
      searchMultiTransports.combineTransform(toStop) { transports, toStop -> emit(transports to toStop) }
    ) {
      val locale = viewModel.locale.value
      val multiRoots = it.first
      val toStop = it.second
      if (multiRoots.isEmpty()) return@observe

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
        submitList(multiRoots)
      }
    }
    observe(fromStop) { stop ->
      val locale = viewModel.locale.value
      binding.tvFromLabel.setStopName(stop, locale)
    }
    observe(toStop) { stop ->
      val locale = viewModel.locale.value
      binding.tvToLabel.setStopName(stop, locale)
    }
  }

  private fun stopNotFound() {
    showToast(getString(R.string.error_stop_not_found))
    router.exit()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance(args: Bundle) = SearchFragment().apply { arguments = args }
  }
}
