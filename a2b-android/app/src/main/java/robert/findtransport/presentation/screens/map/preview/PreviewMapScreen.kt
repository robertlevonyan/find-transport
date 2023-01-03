package robert.findtransport.presentation.screens.map.preview

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.StopLocation
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.presentation.screens.map.MapViewModel
import robert.findtransport.presentation.screens.map.enableLocationComponent
import robert.findtransport.presentation.screens.map.flyTo
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.EMPTY_ID
import robert.findtransport.utils.STOP_ICON_SIZE
import robert.findtransport.utils.extensions.*

@Composable
fun PreviewMapScreen(
  mapStyle: String,
  locationEnabled: Boolean,
  previewMapViewModel: PreviewMapViewModel = hiltViewModel(),
  mapViewModel: MapViewModel,
  navController: NavController,
  transportId: Int,
  underground: Boolean,
  reversed: Boolean,
) {
  if (transportId == EMPTY_ID) {
    navController.popBackStack()
    return
  }
  if (reversed) {
    previewMapViewModel.getReversedTransportRoute(transportId, underground)
  } else {
    previewMapViewModel.getTransportRoute(transportId, underground)
  }

  val scope = rememberCoroutineScope()
  val locale by previewMapViewModel.locale.collectAsState()

  AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = { context ->
      ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)
      MapView(context = context)
    },
    update = { mapView ->
      val map = mapView.getMapboxMap()
      val context = mapView.context

      val pointAnnotationManager = mapView.annotations.createPointAnnotationManager().apply {
        addClickListener(OnPointAnnotationClickListener { pointAnnotation ->
          pointAnnotation.getData()?.let { data ->
            val stop = data.fromJson<Stop>().toStop()
            context.showToast(stop.getCurrentName(locale))
          }
          true
        })
      }
      val polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager().apply {
        lineCap = LineCap.ROUND
      }

      map.loadStyleUri(
        styleUri = mapStyle,
        onStyleLoaded = {
          if (locationEnabled) {
            mapView.enableLocationComponent()
            mapViewModel.getCurrentLocation()
          }

          scope.launch {
            mapViewModel.currentLocation.collectLatest { currentLocation ->
              map.flyTo(currentLocation)
            }
          }

          scope.launch {
            previewMapViewModel.route.collectLatest { result ->
              handleRoute(
                result = result,
                reversed = reversed,
                context = context,
                polylineAnnotationManager = polylineAnnotationManager,
                map = map,
                pointAnnotationManager = pointAnnotationManager,
                navController = navController,
              )
            }
          }
        }
      )
    }
  )
}

private fun handleRoute(
  result: RouteResult,
  reversed: Boolean,
  context: Context,
  polylineAnnotationManager: PolylineAnnotationManager,
  map: MapboxMap,
  pointAnnotationManager: PointAnnotationManager,
  navController: NavController,
) {
  when (result) {
    is RouteResult.Success -> {
      val coordinates = result.transport.run {
        if (reversed) stops else stopsReversed
      }.flatMap { it.coordinates }

      createRoute(context, coordinates, polylineAnnotationManager)

      val padding = context.getDimenInt(R.dimen.fab_margin).toDouble()
      val center = coordinates.getOrNull(coordinates.lastIndex / 2)
        ?.run { Point.fromLngLat(lng, lat) }
        ?: Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)

      map.easeTo(
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

      context.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign)?.let { iconBitmap ->
        val points = coordinates.map { location ->
          PointAnnotationOptions()
            .withPoint(Point.fromLngLat(location.lng, location.lat))
            .withData(location.parentStop.toApiStop().toJson())
            .withIconSize(STOP_ICON_SIZE)
            .withIconImage(iconBitmap)
        }
        pointAnnotationManager.create(points)
      }
    }
    is RouteResult.Failed -> {
      context.showToast(result.message)
      navController.popBackStack()
    }
  }
}

private fun createRoute(
  context: Context,
  coordinates:
  List<StopLocation>,
  polylineAnnotationManager: PolylineAnnotationManager,
) {
  val points = coordinates.map { Point.fromLngLat(it.lng, it.lat) }
  val colorRes = R.color.colorAccent300

  val options = PolylineAnnotationOptions()
    .withLineColor(context.getColorFromRes(colorRes))
    .withLineWidth(5.0)
    .withLineJoin(LineJoin.ROUND)
    .withGeometry(LineString.fromLngLats(points))

  polylineAnnotationManager.create(options)
}
