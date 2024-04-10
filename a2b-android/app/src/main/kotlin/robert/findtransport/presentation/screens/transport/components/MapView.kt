@file:OptIn(MapboxExperimental::class)

package robert.findtransport.presentation.screens.transport.components

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mapbox.geojson.Point
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.extensions.getBitmapFromVectorDrawable
import robert.findtransport.utils.extensions.getLocationComponent

//private var pointAnnotationManager: PointAnnotationManager? = null
//private var polylineAnnotationManager: PolylineAnnotationManager? = null

@Composable
fun MapView(
    locale: String,
    locationEnabled: Boolean,
    mapStyle: String,
    transport: Transport,
    isPrimary: Boolean,
) {
    val context = LocalContext.current
    val locationComponentSettings = getLocationComponent(context, locationEnabled)

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapInitOptionsFactory = { ctx ->
            MapInitOptions(
                context = ctx,
                styleUri = mapStyle,
            )
        },
        mapViewportState = MapViewportState().apply {
            setCameraOptions {
                zoom(11.0)
                center(Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE))
                pitch(0.0)
                bearing(0.0)
            }
        },
        locationComponentSettings = locationComponentSettings,
        content = {

        }
    )


//  AndroidView(
//    modifier = Modifier.fillMaxSize(),
//    factory = { context ->
//      ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)
//      com.mapbox.maps.MapView(context = context)
//    },
//    update = { mapView ->
//      val map = mapView.getMapboxMap()
//      val context = mapView.context
//
////      if (pointAnnotationManager == null) {
//        pointAnnotationManager = mapView.annotations.createPointAnnotationManager().apply {
//          addClickListener(OnPointAnnotationClickListener { pointAnnotation ->
//            pointAnnotation.getData()?.let { data ->
//              val stop = data.fromJson<Stop>().toStop()
//              context.showToast(stop.getCurrentName(locale))
//            }
//            true
//          })
//        }
////      }
////      if (polylineAnnotationManager == null) {
//        polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager().apply {
//          lineCap = LineCap.ROUND
//        }
////      }
//
//      map.loadStyleUri(styleUri = mapStyle, onStyleLoaded = { style ->
//        if (locationEnabled) {
//          mapView.enableLocationComponent()
//        }
//
//        map.setCamera(CameraOptions.Builder().zoom(11.0).build())
//
//        handleRoute(
//          context = context,
//          map = map,
//          transport = transport,
//          isPrimary = isPrimary,
//        )
//      })
//    },
//  )
}

//private fun handleRoute(
//  context: Context,
//  map: MapboxMap,
//  transport: Transport,
//  isPrimary: Boolean,
//) {
//  if (transport == Transport.EMPTY) return
//
//  val coordinates = transport.run {
//    if (isPrimary) stops else stopsReversed
//  }.flatMap { it.coordinates }
//
//  val route = transport.run {
//    if (isPrimary) route.mainRoute else route.reversedRoute
//  }.map { coord ->
//    Point.fromLngLat(coord[0], coord[1])
//  }
//
//  val options =
//    PolylineAnnotationOptions().withLineColor(context.getColorFromRes(R.color.colorAccent300))
//      .withLineWidth(5.0).withLineJoin(LineJoin.ROUND)
//      .withGeometry(LineString.fromLngLats(route.ifEmpty {
//        coordinates.map { Point.fromLngLat(it.lng, it.lat) }
//      }))
//  polylineAnnotationManager?.deleteAll()
//  polylineAnnotationManager?.create(options)
//
//  val padding = FabPadding.value.toDouble()
//  val center = coordinates.getOrNull(coordinates.lastIndex / 2)
//    ?.run { Point.fromLngLat(lng, lat) }
//    ?: Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)
//
//  map.easeTo(
//    cameraOptions = CameraOptions.Builder().zoom(11.0)
//      .padding(EdgeInsets(padding, padding, padding, padding)).center(center).build(),
//    animationOptions = MapAnimationOptions.mapAnimationOptions {
//      duration(200)
//      interpolator(FastOutSlowInInterpolator())
//    },
//  )
//
//  context.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign)?.let { iconBitmap ->
//    val points = coordinates.map { location ->
//      PointAnnotationOptions().withPoint(Point.fromLngLat(location.lng, location.lat))
//        .withData(location.parentStop.toApiStop().toJson()).withIconSize(STOP_ICON_SIZE)
//        .withIconImage(iconBitmap)
//    }
//    pointAnnotationManager?.deleteAll()
//    pointAnnotationManager?.create(points)
//  }
//}
