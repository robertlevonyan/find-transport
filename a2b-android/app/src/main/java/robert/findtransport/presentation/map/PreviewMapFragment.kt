package robert.findtransport.presentation.map

import android.os.Bundle
import androidx.core.os.bundleOf
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.*
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import robert.findtransport.R
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.StopLocation
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toJson
import robert.findtransport.domain.mapper.toStop
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
      val style = mapStyle ?: return@observe

      val coordinates = routeResult.transport.run {
        if (reverse) stops else stopsReversed
      }.flatMap { it.coordinates }

      createRoute(routeResult, style, coordinates)

//      style.addLayer(createRouteLayer())
//      val latLngBounds = createLatLngBounds(coordinates)

//      getDrawableFromRes(R.drawable.ic_stop_sign)?.let { style.addImage(STOP_IMAGE, it) }

      hideLoading()

//      mapboxMap?.let { map ->
//        map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200), 5000)
//        showStops(map, style, coordinates.map { location ->
//          SymbolOptions().apply {
//            withData(location.parentStop.toApiStop().toJson())
//            withLatLng(LatLng(location.lat, location.lng))
//            withIconImage(STOP_IMAGE)
//            withIconSize(STOP_ICON_SIZE)
//          }
//        })
//      }
    }

    observe(routeError) { message ->
      showToast(getString(message))
      router.exit()
    }
  }

  private fun createRoute(routeResult: RouteResult, style: Style, coordinates: List<StopLocation>) {
//    routeResult.route?.run {
//      geometry()?.let { geometry ->
//        val routeSource = GeoJsonSource(ROUTE_SOURCE)
//        if (!style.sources.contains(routeSource)) {
//          style.addSource(routeSource)
//        }
//        style.getSource(ROUTE_SOURCE)
//          .takeIf { it is GeoJsonSource }
//          ?.let { it as GeoJsonSource }
//          ?.setGeoJson(LineString.fromPolyline(geometry, Constants.PRECISION_6))
//      }
//    } ?: style.addSource(createGeoJsonSource(coordinates))
  }

//  private fun createGeoJsonSource(coordinates: List<StopLocation>): Source = GeoJsonSource(
//    ROUTE_SOURCE,
//    FeatureCollection.fromFeatures(
//      arrayOf(
//        Feature.fromGeometry(
//          LineString.fromLngLats(MultiPoint.fromLngLats(coordinates.map { Point.fromLngLat(it.lng, it.lat) }))
//        )
//      )
//    )
//  )
//
//  private fun createRouteLayer(): Layer = LineLayer(ROUTE_LAYER, ROUTE_SOURCE).apply {
//    setProperties(
//      PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//      PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//      PropertyFactory.lineWidth(5f),
//      PropertyFactory.lineColor(getColorFromRes(R.color.colorAccent))
//    )
//  }

//  private fun createLatLngBounds(coordinates: List<StopLocation>): LatLngBounds = LatLngBounds.Builder()
//    .include(coordinates.firstOrNull()?.run { LatLng(lat, lng) } ?: LatLng())
//    .include(coordinates.lastOrNull()?.run { LatLng(lat, lng) } ?: LatLng())
//    .build()

//  private fun showStops(mapboxMap: MapboxMap, style: Style, stops: List<SymbolOptions>) {
//    SymbolManager(binding.mapView, mapboxMap, style).apply {
//      addClickListener { symbol ->
//        symbol?.data?.let { data ->
//          val stop = data.fromJson<robert.findtransport.data.entity.Stop>().toStop()
//          viewModel.getStopName(stop)
//            .takeIf { it != "" }
//            ?.let { name -> view?.showSnackbar(name) }
//        }
//        true
//      }
//      create(stops)
//    }
//  }

  companion object {
    fun newInstance(data: Bundle = bundleOf()) = PreviewMapFragment().apply {
      arguments = data
    }
  }
}
