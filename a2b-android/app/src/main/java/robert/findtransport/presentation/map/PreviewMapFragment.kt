package robert.findtransport.presentation.map

import android.graphics.Color
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.mapbox.geojson.*
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toJson
import robert.findtransport.utils.*
import robert.findtransport.utils.extensions.*

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
        router.exit()
        return
      }
    reverse = arguments?.getBoolean(ARG_ROUTE_REVERSE) ?: false
    val isUnderground = arguments?.getBoolean(ARG_UNDERGROUND) ?: false

    binding.tvTitle.setText(
      if (reverse) {
        viewModel.getTransportRoute(id, isUnderground)
        R.string.label_primary_route
      } else {
        viewModel.getTransportRouteReverse(id, isUnderground)
        R.string.label_secondary_route
      }
    )
  }

  override fun MapViewModel.initObservers() {
    observe(routeSuccess) { routeResult ->
      val coordinates = routeResult.transport.run {
        if (reverse) stops else stopsReversed
      }.flatMap { it.coordinates }

      createRoute(coordinates)

      hideLoading()


      mapboxMap.setBounds(
        CameraBoundsOptions.Builder()
          .bounds(createCoordinateBounds(coordinates))
          .minZoom(11.0)
          .build()
      )

      val padding = getDimenInt(R.dimen.fab_margin).toDouble()
      val center = coordinates.getOrNull(coordinates.lastIndex / 2)
        ?.run { Point.fromLngLat(lng, lat) }
        ?: Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)

      mapboxMap.easeTo(
        cameraOptions = CameraOptions.Builder()
          .zoom(11.0)
          .padding(EdgeInsets(padding, padding, padding, padding))
          .center(center)
          .build(),
        animationOptions = MapAnimationOptions.mapAnimationOptions {
          duration(200)
          interpolator(FastOutSlowInInterpolator())
        },
      )

      val iconBitmap = context?.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign) ?: return@observe

      val points = coordinates.map { location ->
        PointAnnotationOptions()
          .withPoint(Point.fromLngLat(location.lng, location.lat))
          .withData(location.parentStop.toApiStop().toJson())
          .withIconSize(STOP_ICON_SIZE)
          .withIconImage(iconBitmap)
      }

      pointAnnotationManager.create(points)
    }

    observe(routeError) { message ->
      showToast(getString(message))
      router.exit()
    }
  }

  private fun createRoute(coordinates: List<StopLocation>) {
    val points = coordinates.map { Point.fromLngLat(it.lng, it.lat) }
    val options = PolylineAnnotationOptions()
      .withLineColor(context?.getColorFromRes(R.color.colorAccent) ?: Color.YELLOW)
      .withLineWidth(5.0)
      .withLineJoin(LineJoin.ROUND)
      .withGeometry(LineString.fromLngLats(points))

    polylineAnnotationManager.create(options)
  }

  override fun showStopOptions(stop: Stop) {
    super.showStopOptions(stop)
    viewModel.getStopName(stop).let { name ->
      view?.showSnackbar(name)
    }
  }

  private fun createCoordinateBounds(coordinates: List<StopLocation>): CoordinateBounds {
    val (southwestLng, southwestLat) = coordinates.firstOrNull()?.run { lng to lat } ?: DEFAULT_LONGITUDE to DEFAULT_LATITUDE
    val (northeastLng, northeastLat) = coordinates.lastOrNull()?.run { lng to lat } ?: DEFAULT_LONGITUDE to DEFAULT_LATITUDE
    return CoordinateBounds(
      Point.fromLngLat(southwestLng, southwestLat),
      Point.fromLngLat(northeastLng, northeastLat),
      false,
    )
  }

  companion object {
    fun newInstance(data: Bundle = bundleOf()) = PreviewMapFragment().apply {
      arguments = data
    }
  }
}
