package robert.findtransport.utils.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Location
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.core.net.toUri
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType.*
import robert.findtransport.presentation.reusables.Text20
import robert.findtransport.utils.*
import java.math.BigInteger
import java.security.MessageDigest

fun Transport.getTypeName() = when (type) {
  MICROBUS_OLD, MICROBUS_NEW, MICROBUS_SPRINTER -> R.string.label_microbus
  BUS_BOGDAN, BUS_VIOLET, BUS_JONGTONG, BUS_MAN, BUS_HYUNDAI, BUS_PAZ -> R.string.label_bus
  TROLLEYBUS_OLD, TROLLEYBUS_NEW -> R.string.label_trolleybus
  METRO -> R.string.label_metro
  UNDEFINED -> -1
}

fun Stop.getCurrentName(locale: String): String = when (locale) {
  LNG_EN -> nameEn
  LNG_RU -> nameRu
  else -> nameAm
}

fun Transport.correctStops(start: Stop, destination: Stop): List<Stop> {
  var which = 0
  val stopIds = stops.map { it.id }

  var startPosition = -1
  var destPosition = -1
  for (i in stopIds.indices) {
    val stopId = stopIds[i]
    if (stopId == start.id) {
      startPosition = i
    }
    if (stopId == destination.id) {
      destPosition = i
    }
  }

  if (startPosition != -1 && destPosition != -1 && startPosition > destPosition) {
    which = 1
  }

  if (which == 0) {
    val stopReverseIds = stopsReversed.map { it.id }
    for (i in stopReverseIds.indices) {
      val stopId = stopReverseIds[i]
      if (stopId == start.id) {
        destPosition = i
      }
      if (stopId == destination.id) {
        startPosition = i
      }
    }
    if (startPosition != -1 && destPosition != -1 && startPosition < destPosition) {
      which = 2
    }
  }

  return if (which == 2) stopsReversed else stops
}

fun md5(vararg values: Any): String {
  var input = ""
  values.forEach {
    input += it.toString()
  }
  val md = MessageDigest.getInstance("MD5")
  return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

fun Transport.getIcon(): Int = when (type) {
  MICROBUS_OLD -> R.drawable.ic_microbus_old
  MICROBUS_NEW -> R.drawable.ic_microbus_new
  BUS_BOGDAN -> R.drawable.ic_bus_bogdan
  BUS_VIOLET -> R.drawable.ic_bus_violet
  BUS_JONGTONG -> R.drawable.ic_bus_jongtong
  BUS_MAN -> R.drawable.ic_bus_man
  TROLLEYBUS_OLD -> R.drawable.ic_trolleybus
  TROLLEYBUS_NEW -> R.drawable.ic_trolleybus
  METRO -> R.drawable.ic_metro
  BUS_HYUNDAI -> R.drawable.ic_bus_hyundai
  BUS_PAZ -> R.drawable.ic_bus_paz
  MICROBUS_SPRINTER -> R.drawable.ic_microbus_sprinter
  UNDEFINED -> R.drawable.ic_bus_violet
}

fun Transport.getNameFormatted(): AnnotatedString = buildAnnotatedString {
  if (number.endsWith("ա")) {
    append(number.substring(0, number.lastIndex))
    withStyle(
      SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontSize = Text20,
      )
    ) {
      append("ա")
    }
  } else {
    append(number)
  }
}

fun Context.openPrivacyPolicy() {
  try {
    val url = "https://www.freeprivacypolicy.com/privacy/view/58828427193536dad1fea738a43a0758"
    CustomTabsIntent.Builder().run {
      setDefaultColorSchemeParams(
        CustomTabColorSchemeParams.Builder()
          .setToolbarColor(getColorFromRes(R.color.colorPrimary))
          .setSecondaryToolbarColor(getColorFromRes(R.color.colorOnPrimary))
          .build()
      )
      build()
    }.launchUrl(this, url.toUri())
  } catch (e: Exception) {
    e.printStackTrace()
    showToast("Google Chrome cannot be found")
  }
}

fun MapView.disableAllGestures() {
  with(gestures) {
    rotateEnabled = false
    pinchToZoomEnabled = false
    scrollEnabled = false
    simultaneousRotateAndPinchToZoomEnabled = false
    pitchEnabled = false
    doubleTapToZoomInEnabled = false
    doubleTouchToZoomOutEnabled = false
    quickZoomEnabled = false
    pinchToZoomDecelerationEnabled = false
    rotateDecelerationEnabled = false
    scrollDecelerationEnabled = false
    increasePinchToZoomThresholdWhenRotating = false
    pinchScrollEnabled = false
  }
}

fun Address.toLocation(): Location = Location(featureName).also {
  it.latitude = if (hasLatitude()) latitude else DEFAULT_LATITUDE
  it.longitude = if (hasLongitude()) longitude else DEFAULT_LONGITUDE
}

fun StopLocation.toLocation(): Location = Location(parentStop.nameEn).also {
  it.latitude = lat
  it.longitude = lng
}

fun Address.getFormattedAddress(locale: String): String {
  if (thoroughfare == null || featureName == null) return ""
  return when (locale) {
    LNG_AM -> "$thoroughfare $featureName"
    LNG_RU -> "$thoroughfare $featureName"
    else -> "$featureName $thoroughfare"
  }
}

fun Location.toPoint(): Point = Point.fromLngLat(longitude, latitude, altitude)

fun MapView.enableLocationComponent() {
  location.updateSettings {
    enabled = true
    pulsingEnabled = false
    pulsingColor = context?.getColorFromRes(R.color.colorAccent300) ?: Color.YELLOW
    locationPuck = LocationPuck2D().apply {
      topImage =
        BitmapDrawable(resources, context?.getBitmapFromVectorDrawable(R.drawable.ic_bearing))
    }
  }
}

fun MapboxMap.flyTo(location: Location) {
  try {
    flyTo(
      cameraOptions = CameraOptions.Builder()
        .center(Point.fromLngLat(location.longitude, location.latitude)).zoom(15.0).build(),
      animationOptions = MapAnimationOptions.mapAnimationOptions {
        duration(duration = 200)
        interpolator(interpolator = FastOutSlowInInterpolator())
      },
    )
  } catch (e: Exception) {
    e.printStackTrace()
  }
}
