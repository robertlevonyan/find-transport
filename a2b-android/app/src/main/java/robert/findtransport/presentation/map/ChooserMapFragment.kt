package robert.findtransport.presentation.map

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.mapbox.maps.Style
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.di.passingRoutesScreen
import robert.findtransport.presentation.component.bottomsheet.map.StopOptionsBottomSheet
import robert.findtransport.utils.ARG_STOP
import robert.findtransport.utils.RESULT_FROM
import robert.findtransport.utils.RESULT_TO

@AndroidEntryPoint
class ChooserMapFragment : MapFragment() {

  override fun createMap(style: Style) {
    viewModel.getStops()
  }

  override fun MapViewModel.initObservers() {
    observe(currentLocation) { location ->
      flyTo(location.latitude, location.longitude)
    }

    observe(allStops) { stops ->
      if (stops.isEmpty()) return@observe
      pointAnnotationManager.create(stops)
      hideLoading()
    }
    observe(metroStops) { stops ->
      if (stops.isEmpty()) return@observe
      pointAnnotationManager.create(stops)
      hideLoading()
    }
  }

  override fun showStopOptions(stop: robert.findtransport.data.model.Stop) {
    StopOptionsBottomSheet.newInstance(bundleOf(ARG_STOP to stop.id)).apply {
      onFromSelected = { selectedStop ->
        this@ChooserMapFragment.viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
          setFragmentResult(RESULT_FROM, bundleOf(RESULT_FROM to selectedStop.id))
          router.exit()
        }
      }
      onToSelected = { selectedStop ->
        this@ChooserMapFragment.viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
          setFragmentResult(RESULT_TO, bundleOf(RESULT_TO to selectedStop.id))
          router.exit()
        }
      }
      onShowTransports = { selectedStop ->
        router.navigateTo(passingRoutesScreen(selectedStop.id))
      }
    }.show(parentFragmentManager, StopOptionsBottomSheet::class.java.simpleName)
  }

  companion object {
    fun newInstance() = ChooserMapFragment()
  }
}
