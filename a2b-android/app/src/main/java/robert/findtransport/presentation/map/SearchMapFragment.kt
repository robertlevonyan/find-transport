package robert.findtransport.presentation.map

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.mapbox.geojson.*
import com.mapbox.maps.Style
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.data.model.*
import robert.findtransport.utils.ARG_FROM_ID
import robert.findtransport.utils.ARG_TO_ID
import robert.findtransport.utils.extensions.showInfiniteSnackbar

@AndroidEntryPoint
class SearchMapFragment : MapFragment() {
  private val searchMapViewModel: SearchMapViewModel by viewModels()
  private val loadingSnackbar by lazy { view?.showInfiniteSnackbar(R.string.message_loading_data) }
  private var mapStyle: Style? = null

  override fun createMap(style: Style) {
    mapStyle = style

    val fromId = arguments?.getInt(ARG_FROM_ID)
    val toId = arguments?.getInt(ARG_TO_ID)

    if (fromId == null || toId == null) {
      router.exit()
      return
    }

    searchMapViewModel.getMultiRoute(fromId, toId)

    searchMapViewModel.run {
      collectWithLifecycle(loading) { isLoading -> loadingSnackbar?.run { if (isLoading) show() else dismiss() } }
      collectWithLifecycle(searchMultiTransports) { onDataLoaded(it.first/*, it.second, it.third*/) }
      collectWithLifecycle(searchEmpty) { router.exit() }
    }
  }

  private fun onDataLoaded(multiRouteData: List<MultiRoute>/*, from: Stop, to: Stop*/) {
//    val style = mapStyle ?: return
    val startCoordinates = mutableListOf<StopLocation>()
    val endCoordinates = mutableListOf<StopLocation>()
    val interchangeCoordinates = mutableListOf<StopLocation>()
    val transports = mutableListOf<Transport>()

    multiRouteData.groupBy { it.case }.entries.forEach { entry ->
      when (entry.key) {
        MultiRouteCase.SINGLE_FROM -> {
          entry.value.forEach { multiRoot ->
            when (multiRoot.type) {
              MultiType.TRANSPORT_TITLE -> {
                // start
                multiRoot.stop?.coordinates?.forEach(startCoordinates::add)
              }
              MultiType.TRANSPORT -> {
                // transports
                multiRoot.transport?.let(transports::add)
              }
              MultiType.INTERCHANGE_TO -> {
                // interchange
                multiRoot.stop?.coordinates?.forEach(interchangeCoordinates::add)
              }
              MultiType.WALK_TO -> {
                // destination
                multiRoot.stop?.coordinates?.forEach(endCoordinates::add)
              }
              else -> Unit
            }
          }

          transports.firstOrNull()?.let { firstTransport ->
            searchMapViewModel.getRouteSuccess(firstTransport.id)
          }

          collectWithLifecycle(searchMapViewModel.transportRouteSuccess) { _ ->
//            val routeResult = routeSuccess.first ?: return@collectWithLifecycle
//            val routeReverse = routeSuccess.second ?: return@collectWithLifecycle

        //            val transportMainCoordinates = routeResult.transport.stops.flatMap { it.coordinates }
        //            val transportReverseCoordinates = routeReverse.transport.stops.flatMap { it.coordinates }
        //
        //            var startIndex = transportMainCoordinates.indexOf(startCoordinates.firstOrNull() ?: return@observe)
        //            if (startIndex == -1) {
        //              startIndex = transportReverseCoordinates.indexOf(startCoordinates.firstOrNull() ?: return@observe)
        //            }
        //
        //            var endIndex = transportMainCoordinates.indexOf(interchangeCoordinates.firstOrNull() ?: return@observe)
        //            if (endIndex == -1) {
        //              endIndex = transportReverseCoordinates.indexOf(interchangeCoordinates.firstOrNull() ?: return@observe)
        //            }
        //
        //            val newRoute = transportMainCoordinates.subList(startIndex, endIndex)

        //            createRoute(routeResult, style, newRoute)
          }
        }
        MultiRouteCase.SINGLE_TO -> {
          entry.value.forEach { multiRoot ->
            when (multiRoot.type) {
              MultiType.WALK_FROM -> {
                // start
              }
              MultiType.INTERCHANGE_FROM -> {
                // interchange
              }
              MultiType.TRANSPORT -> {
                // transports
              }
              else -> Unit
            }
          }
        }
        MultiRouteCase.FROM_TO -> {
          entry.value.forEach { multiRoot ->
            when (multiRoot.type) {
              MultiType.TRANSPORT_TITLE -> {
                // start
              }
              MultiType.TRANSPORT -> {
                // transports
              }
              MultiType.INTERCHANGE_TO -> {
                // interchange
              }
              MultiType.WALK_TO -> {
                // destination
              }
              else -> Unit
            }
          }
        }
      }
    }

//    val boundsCoordinates = startCoordinates + endCoordinates
//    val latLngBounds = createLatLngBounds(boundsCoordinates)

    hideLoading()

//    getDrawableFromRes(R.drawable.ic_stop_sign)?.let { style.addImage(STOP_IMAGE, it) }
//    style.addLayer(createRouteLayer())

//    mapboxMap?.let { map ->
//      map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200), 5000)
//      showStops(map, style, boundsCoordinates.map { location ->
//        SymbolOptions().apply {
//          withData(location.parentStop.toApiStop().toJson())
//          withLatLng(LatLng(location.lat, location.lng))
//          withIconImage(STOP_IMAGE)
//          withIconSize(STOP_ICON_BIG_SIZE)
//        }
//      })
//      showStops(map, style, interchangeCoordinates.map { location ->
//        SymbolOptions().apply {
//          withData(location.parentStop.toApiStop().toJson())
//          withLatLng(LatLng(location.lat, location.lng))
//          withIconImage(STOP_IMAGE)
//          withIconSize(STOP_ICON_SIZE)
//        }
//      })
//    }
  }

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

//  private fun createRouteLayer(): Layer = LineLayer(ROUTE_LAYER, ROUTE_SOURCE).apply {
//    setProperties(
//      PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//      PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//      PropertyFactory.lineWidth(5f),
//      PropertyFactory.lineColor(getColorFromRes(R.color.colorAccent))
//    )
//  }

//  private fun createRoute(routeResult: RouteResult, style: Style, coordinates: List<StopLocation>) {
//    routeResult.route?.run {
//      geometry()?.let { geometry ->
//        style.addSource(GeoJsonSource(ROUTE_SOURCE))
//        style.getSource(ROUTE_SOURCE)
//          .takeIf { it is GeoJsonSource }
//          ?.let { it as GeoJsonSource }
//          ?.setGeoJson(LineString.fromPolyline(geometry, Constants.PRECISION_6))
//      }
//    } ?: style.addSource(createGeoJsonSource(coordinates))
//  }

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

  companion object {
    fun newInstance(data: Bundle) = SearchMapFragment().apply {
      arguments = data
    }
  }
}
