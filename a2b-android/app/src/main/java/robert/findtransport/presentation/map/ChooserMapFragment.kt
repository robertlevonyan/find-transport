package robert.findtransport.presentation.map

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.coroutineScope
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import robert.findtransport.R
import robert.findtransport.data.entity.Stop
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.presentation.component.bottomsheet.map.StopOptionsBottomSheet
import robert.findtransport.presentation.passing.PassingRoutesFragment
import robert.findtransport.utils.ARG_STOP
import robert.findtransport.utils.RESULT_FROM
import robert.findtransport.utils.RESULT_TO
import robert.findtransport.utils.extensions.addWithSlide
import robert.findtransport.utils.extensions.getDrawableFromRes

class ChooserMapFragment : MapFragment() {

  override fun createMap(style: Style) {
    getDrawableFromRes(R.drawable.ic_stop_sign)?.let { style.addImage(STOP_IMAGE, it) }
    getDrawableFromRes(R.drawable.ic_metro_sign)?.let { style.addImage(METRO_IMAGE, it) }
    viewModel.getStops()
  }

  override fun MapViewModel.initObservers() {
    observe(allStops) { stops ->
      lifecycle.coroutineScope.launchWhenCreated {
        mapboxMap?.let { map ->
          showStops(map, map.style ?: return@let, stops)
          hideLoading()
        }
      }
    }
    observe(metroStops) { stops ->
      lifecycle.coroutineScope.launchWhenCreated {
        mapboxMap?.let { map ->
          showMetroStops(map, map.style ?: return@let, stops)
          hideLoading()
        }
      }
    }
  }

  private fun showMetroStops(mapboxMap: MapboxMap, style: Style, stops: List<SymbolOptions>) {
    SymbolManager(binding.mapView, mapboxMap, style).apply {
      addClickListener { symbol ->
        symbol?.data?.let { data ->
          showStopOptions(data.fromJson<Stop>().toStop())
        }
        true
      }
      create(stops)
    }
  }

  private fun showStops(mapboxMap: MapboxMap, style: Style, stops: List<SymbolOptions>) {
    SymbolManager(binding.mapView, mapboxMap, style).apply {
      addClickListener { symbol ->
        symbol?.data?.let { data -> showStopOptions(data.fromJson<Stop>().toStop()) }
        true
      }
      create(stops)
    }
  }

  private fun showStopOptions(stop: robert.findtransport.data.model.Stop) {
    StopOptionsBottomSheet.newInstance(bundleOf(ARG_STOP to stop.id)).apply {
      onFromSelected = { selectedStop ->
        this@ChooserMapFragment.viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
          setFragmentResult(RESULT_FROM, bundleOf(RESULT_FROM to selectedStop.id))
          parentFragmentManager.popBackStack()
        }
      }
      onToSelected = { selectedStop ->
        this@ChooserMapFragment.viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
          setFragmentResult(RESULT_TO, bundleOf(RESULT_TO to selectedStop.id))
          parentFragmentManager.popBackStack()
        }
      }
      onShowTransports = { selectedStop ->
        addWithSlide(PassingRoutesFragment.newInstance(selectedStop.id))
      }
    }.show(parentFragmentManager, StopOptionsBottomSheet::class.java.simpleName)
  }

  companion object {
    fun newInstance() = ChooserMapFragment()
  }
}
