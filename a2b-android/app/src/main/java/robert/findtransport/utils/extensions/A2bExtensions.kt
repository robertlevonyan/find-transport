package robert.findtransport.utils.extensions

import android.content.Context
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType.*
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import java.math.BigInteger
import java.security.MessageDigest

fun Transport.getTypeName() = when (type) {
  MICROBUS_OLD, MICROBUS_NEW, MICROBUS_SPRINTER -> R.string.label_microbus
  BUS_BOGDAN, BUS_VIOLET, BUS_JONGTONG, BUS_MAN, BUS_HYUNDAI, BUS_PAZ -> R.string.label_bus
  TROLLEYBUS_OLD, TROLLEYBUS_NEW -> R.string.label_trolleybus
  METRO -> R.string.label_underground
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