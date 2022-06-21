package robert.findtransport.presentation.map

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.mapbox.maps.Style
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.presentation.component.bottomsheet.map.StopOptionsBottomSheet
import robert.findtransport.utils.*

@AndroidEntryPoint
class ChooserMapFragment : MapFragment() {
  private var selectedLatitude: Double? = null
  private var selectedLongitude: Double? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    selectedLatitude = arguments?.getDouble(ARG_LATITUDE)
    selectedLongitude = arguments?.getDouble(ARG_LONGITUDE)
  }

  override fun createMap(style: Style) {
    viewModel.getStops()
  }

  override fun MapViewModel.initObservers() {
    collectWithLifecycle(currentLocation) { location ->
      val latitude = selectedLatitude ?: location.latitude
      val longitude = selectedLongitude ?: location.longitude

      flyTo(latitude, longitude)
    }

    collectWithLifecycle(allStops) { stops ->
      if (stops.isEmpty()) return@collectWithLifecycle

      pointAnnotationManager?.create(stops)
      hideLoading()
    }
    collectWithLifecycle(metroStops) { stops ->
      if (stops.isEmpty()) return@collectWithLifecycle

      pointAnnotationManager?.create(stops)
      hideLoading()
    }
  }

  override fun showStopOptions(stop: robert.findtransport.data.model.Stop) {
    StopOptionsBottomSheet.newInstance(bundleOf(ARG_STOP to stop.id)).apply {
      onFromSelected = { selectedStop ->
        this@ChooserMapFragment.viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
          setFragmentResult(RESULT_FROM, bundleOf(RESULT_FROM to selectedStop.id))
//          router.exit()
        }
      }
      onToSelected = { selectedStop ->
        this@ChooserMapFragment.viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
          setFragmentResult(RESULT_TO, bundleOf(RESULT_TO to selectedStop.id))
//          router.exit()
        }
      }
      onShowTransports = { selectedStop ->
//        router.navigateTo(passingRoutesScreen(selectedStop.id))
      }
    }.show(parentFragmentManager, StopOptionsBottomSheet::class.java.simpleName)
  }

  companion object {
    fun newInstance(coordinates: Pair<Double, Double>?) = ChooserMapFragment().apply {
      if (coordinates == null) return@apply

      arguments = bundleOf(
        ARG_LATITUDE to coordinates.first,
        ARG_LONGITUDE to coordinates.second
      )
    }
  }
}
