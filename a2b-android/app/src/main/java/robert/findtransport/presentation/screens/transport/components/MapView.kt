package robert.findtransport.presentation.screens.transport.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.STOP_ICON_SIZE
import robert.findtransport.utils.extensions.enableLocationComponent
import robert.findtransport.utils.extensions.getBitmapFromVectorDrawable
import robert.findtransport.utils.extensions.getColorFromRes
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.showToast

private var pointAnnotationManager: PointAnnotationManager? = null
private var polylineAnnotationManager: PolylineAnnotationManager? = null

@Composable
fun MapView(
  locale: String,
  locationEnabled: Boolean,
  mapStyle: String,
  transport: Transport,
  isPrimary: Boolean,
) {
  AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = { context ->
      ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)
      com.mapbox.maps.MapView(context = context)
    },
    update = { mapView ->
      val map = mapView.getMapboxMap()
      val context = mapView.context

      if (pointAnnotationManager == null) {
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager().apply {
          addClickListener(OnPointAnnotationClickListener { pointAnnotation ->
            pointAnnotation.getData()?.let { data ->
              val stop = data.fromJson<Stop>().toStop()
              context.showToast(stop.getCurrentName(locale))
            }
            true
          })
        }
      }
      if (polylineAnnotationManager == null) {
        polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager().apply {
          lineCap = LineCap.ROUND
        }
      }

      map.loadStyleUri(styleUri = mapStyle, onStyleLoaded = { style ->
        if (locationEnabled) {
          mapView.enableLocationComponent()
        }

        map.setCamera(CameraOptions.Builder().zoom(11.0).build())

        handleRoute(
          context = context,
          map = map,
          transport = transport,
          isPrimary = isPrimary,
        )
      })
    },
  )
}

private fun handleRoute(
  context: Context,
  map: MapboxMap,
  transport: Transport,
  isPrimary: Boolean,
) {
  val coordinates = transport.run {
    if (isPrimary) stops else stopsReversed
  }.flatMap { it.coordinates }

  val route = transport.run {
    if (isPrimary) route.mainRoute else route.reversedRoute
  }.map { coord ->
    Point.fromLngLat(coord[0], coord[1])
  }

  val options =
    PolylineAnnotationOptions().withLineColor(context.getColorFromRes(R.color.colorAccent300))
      .withLineWidth(5.0).withLineJoin(LineJoin.ROUND)
      .withGeometry(LineString.fromLngLats(route.ifEmpty {
        coordinates.map { Point.fromLngLat(it.lng, it.lat) }
      }))
  polylineAnnotationManager?.deleteAll()
  polylineAnnotationManager?.create(options)

  val padding = FabPadding.value.toDouble()
  val center = coordinates.getOrNull(coordinates.lastIndex / 2)
    ?.run { Point.fromLngLat(lng, lat) }
    ?: Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)

  map.easeTo(
    cameraOptions = CameraOptions.Builder().zoom(11.0)
      .padding(EdgeInsets(padding, padding, padding, padding)).center(center).build(),
    animationOptions = MapAnimationOptions.mapAnimationOptions {
      duration(200)
      interpolator(FastOutSlowInInterpolator())
    },
  )

  context.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign)?.let { iconBitmap ->
    val points = coordinates.map { location ->
      PointAnnotationOptions().withPoint(Point.fromLngLat(location.lng, location.lat))
        .withData(location.parentStop.toApiStop().toJson()).withIconSize(STOP_ICON_SIZE)
        .withIconImage(iconBitmap)
    }
    pointAnnotationManager?.deleteAll()
    pointAnnotationManager?.create(points)
  }
}
