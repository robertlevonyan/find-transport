package robert.findtransport.utils.extensions

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.animation.doOnStart
import androidx.core.net.toUri
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.NearbyStopStatus
import robert.findtransport.data.model.enums.TransportType.*
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import java.math.BigInteger
import java.security.MessageDigest

fun Transport.getTypeName() = when (type) {
  BUS -> R.string.label_bus
  MICROBUS -> R.string.label_microbus
  TROLLEYBUS -> R.string.label_trolleybus
  METRO -> R.string.label_underground
  UNDEFINED -> -1
}

fun TextView.setStopName(stop: Stop, locale: String) {
  val value = when (locale) {
    LNG_EN -> stop.nameEn
    LNG_RU -> stop.nameRu
    else -> stop.nameAm
  }
  text = value
}

fun TextView.setDisappearingError(error: Int) {
  alpha = 0f
  ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f).apply {
    doOnStart { setText(error) }
    duration = 2000
  }.start()
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

fun StopLocation.asPair(): Pair<Double, Double> = lat to lng

fun md5(vararg values: Any): String {
  var input = ""
  values.forEach {
    input += it.toString()
  }
  val md = MessageDigest.getInstance("MD5")
  return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

fun Transport.getIcon(): Int = when (type) {
  BUS -> if (isNew) {
    R.drawable.ic_new_bus
  } else {
    R.drawable.ic_bus
  }
  MICROBUS -> if (isNew) {
    R.drawable.ic_new_microbus
  } else {
    R.drawable.ic_microbus
  }
  TROLLEYBUS -> R.drawable.ic_trolleybus
  METRO -> R.drawable.ic_metro
  UNDEFINED -> R.drawable.ic_bus
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