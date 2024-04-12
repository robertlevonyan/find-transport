@file:OptIn(MapboxExperimental::class)

package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import robert.findtransport.R
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.presentation.reusables.composables.getMapStyle
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.STOP_ICON_SIZE
import robert.findtransport.utils.extensions.getBitmapFromVectorDrawable
import robert.findtransport.utils.extensions.getColorFromRes
import robert.findtransport.utils.extensions.getCompass
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getLocationComponent
import robert.findtransport.utils.extensions.showToast

@Composable
fun MapComponent(
    locale: String,
    locationEnabled: Boolean,
    transport: Transport,
    isPrimary: Boolean,
) {
    val context = LocalContext.current
    val locationComponentSettings = getLocationComponent(context, locationEnabled)
    val compassSettings = getCompass()
    val mapStyle = getMapStyle()

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
        compassSettings = compassSettings,
        content = {
            if (transport == Transport.EMPTY) return@MapboxMap

            val coordinates = transport.run {
                if (isPrimary) stops else stopsReversed
            }.flatMap { it.coordinates }

            val route = transport.run {
                if (isPrimary) route.mainRoute else route.reversedRoute
            }.map { coord ->
                Point.fromLngLat(coord[0], coord[1])
            }

            PolylineAnnotation(
                points = coordinates.map { coord -> Point.fromLngLat(coord.lng, coord.lat) },
                lineColorInt = context.getColorFromRes(R.color.colorAccent300),
                lineWidth = 5.0,
                lineJoin = LineJoin.ROUND,
            )

            context.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign)?.let { iconBitmap ->
                val points = coordinates.map { location ->
                    PointAnnotationOptions().withPoint(Point.fromLngLat(location.lng, location.lat))
                        .withData(location.parentStop.toApiStop().toJson())
                        .withIconSize(STOP_ICON_SIZE)
                        .withIconImage(iconBitmap)
                }

                PointAnnotationGroup(
                    annotations = points,
                    onClick = { pointAnnotation ->
                        pointAnnotation.getData()?.let { data ->
                            val stop = data.fromJson<Stop>().toStop()
                            context.showToast(stop.getCurrentName(locale))
                        }
                        true
                    }
                )
            }
        }
    )
}
