package robert.findtransport.presentation.map

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.os.bundleOf
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.data.model.StopLocation
import robert.findtransport.presentation.screens.map.MapViewModel
import robert.findtransport.utils.ARG_ROUTE_REVERSE
import robert.findtransport.utils.ARG_TRANSPORT_ID
import robert.findtransport.utils.ARG_UNDERGROUND
import robert.findtransport.utils.extensions.getColorFromRes

@AndroidEntryPoint
class PreviewMapFragment : MapFragment() {
  private var reverse = false
  private var mapStyle: Style? = null

  override fun createMap(style: Style) {
    var id = 0
    mapStyle = style
    arguments
      ?.takeIf { it.containsKey(ARG_TRANSPORT_ID) }
      ?.run { getInt(ARG_TRANSPORT_ID) }
      ?.takeIf { it != -1 }
      ?.let { id = it }
      ?: run {
//        router.exit()
        return
      }
    reverse = arguments?.getBoolean(ARG_ROUTE_REVERSE) ?: false
    val isUnderground = arguments?.getBoolean(ARG_UNDERGROUND) ?: false

//    binding.tvTitle.setText(
//      if (reverse) {
//        viewModel.getTransportRoute(id, isUnderground)
//        R.string.label_primary_route
//      } else {
//        viewModel.getTransportRouteReverse(id, isUnderground)
//        R.string.label_secondary_route
//      }
//    )
  }

  override fun MapViewModel.initObservers() {
    collectWithLifecycle(currentLocation) { location ->
//      flyTo(location.latitude, location.longitude)
    }

//    collectWithLifecycle(routeSuccess) { routeResult ->
//      val coordinates = routeResult.transport.run {
//        if (reverse) stops else stopsReversed
//      }.flatMap { it.coordinates }
//
//      createRoute(coordinates)
//
//      hideLoading()
//
//      val padding = getDimenInt(R.dimen.fab_margin).toDouble()
//      val center = coordinates.getOrNull(coordinates.lastIndex / 2)
//        ?.run { Point.fromLngLat(lng, lat) }
//        ?: Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)
//
//      mapboxMap?.easeTo(
//        cameraOptions = CameraOptions.Builder()
//          .zoom(11.0)
//          .padding(EdgeInsets(padding, padding, padding, padding))
//          .center(center)
//          .build(),
//        animationOptions = MapAnimationOptions.mapAnimationOptions {
//          duration(200)
//          interpolator(FastOutSlowInInterpolator())
//        },
//      )
//
//      val iconBitmap = context?.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign) ?: return@collectWithLifecycle
//
//      val points = coordinates.map { location ->
//        PointAnnotationOptions()
//          .withPoint(Point.fromLngLat(location.lng, location.lat))
//          .withData(location.parentStop.toApiStop().toJson())
//          .withIconSize(STOP_ICON_SIZE)
//          .withIconImage(iconBitmap)
//      }
//
//      pointAnnotationManager?.create(points)
//    }

//    collectWithLifecycle(routeError) { message ->
//      showToast(getString(message))
//      router.exit()
//    }
  }

  private fun createRoute(coordinates: List<StopLocation>) {
    val points = coordinates.map { Point.fromLngLat(it.lng, it.lat) }
    val colorRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      android.R.color.system_accent1_300
    } else {
      R.color.colorAccent300
    }
    val options = PolylineAnnotationOptions()
      .withLineColor(context?.getColorFromRes(colorRes) ?: Color.YELLOW)
      .withLineWidth(5.0)
      .withLineJoin(LineJoin.ROUND)
      .withGeometry(LineString.fromLngLats(points))

    polylineAnnotationManager?.create(options)
  }

//  override fun showStopOptions(stop: Stop) {
//    super.showStopOptions(stop)
//    viewModel.getStopName(stop).let { name ->
//      view?.showSnackbar(name)
//    }
//  }

  companion object {
    fun newInstance(data: Bundle = bundleOf()) = PreviewMapFragment().apply {
      arguments = data
    }
  }
}
