package robert.findtransport.utils.extensions

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.animation.doOnStart
import androidx.core.net.toUri
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.LocationPermission
import robert.findtransport.data.model.enums.TransportType.*
import robert.findtransport.presentation.compose.screens.transport.TransportViewModel
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.setTransportIcon(transport: Transport) {
  when (transport.type) {
    BUS -> if (transport.isNew) setImageResource(R.drawable.ic_new_bus) else setImageResource(R.drawable.ic_bus)
    MICROBUS -> if (transport.isNew) setImageResource(R.drawable.ic_new_microbus) else setImageResource(R.drawable.ic_microbus)
    TROLLEYBUS -> setImageResource(R.drawable.ic_trolleybus)
    METRO -> setImageResource(R.drawable.ic_metro)
    UNDEFINED -> return
  }
}

fun TextView.setTransportType(transport: Transport) {
  val typeName = transport.getTypeName()
  if (typeName == -1) return
  setText(typeName)
}

fun Transport.getTypeName() = when (type) {
  BUS -> R.string.label_bus
  MICROBUS -> R.string.label_microbus
  TROLLEYBUS -> R.string.label_trolleybus
  METRO -> R.string.label_underground
  UNDEFINED -> -1
}

fun TextView.setFirstLastStop(transport: Transport, locale: String) =
  transport.takeIf { it.stops.isNotEmpty() }?.run {
    val firstStop = stops.first()
    val lastStop = stops.last()

    val value = when (locale) {
      LNG_EN -> "${firstStop.nameEn} - ${lastStop.nameEn}"
      LNG_RU -> "${firstStop.nameRu} - ${lastStop.nameRu}"
      else -> "${firstStop.nameAm} - ${lastStop.nameAm}"
    }

    text = value
  }

fun TextView.setStopName(stop: Stop, locale: String) {
  val value = when (locale) {
    LNG_EN -> stop.nameEn
    LNG_RU -> stop.nameRu
    else -> stop.nameAm
  }
  text = value
}

fun ImageView.setLocationIcon(permission: LocationPermission) {
  var res = R.drawable.ic_current_location_black
  var ended = false
  val anim = animate().alpha(1f).apply {
    duration = 500
    doOnEnd {
      if (ended) return@doOnEnd
      start()
      res = if (res == R.drawable.ic_current_location_black) {
        R.drawable.ic_current_location_color
      } else {
        R.drawable.ic_current_location_black
      }
      setImageResource(res)
    }
  }
  when (permission) {
    LocationPermission.LOADING -> {
      anim.start()
    }
    LocationPermission.HAS_PERMISSION -> {
      ended = true
      clearAnimation()
      anim.cancel()
      setImageResource(R.drawable.ic_current_location_color)
    }
    LocationPermission.NO_PERMISSION -> {
      clearAnimation()
      anim.cancel()
      setImageResource(R.drawable.ic_current_location_black)
    }
    LocationPermission.UNDEFINED -> return
  }
}

fun View.setStopOptionsMenu(viewModel: TransportViewModel?, stop: Stop) {
  PopupMenu(context, this)
    .apply {
      menuInflater.inflate(R.menu.menu_route, menu)
      setOnMenuItemClickListener {
        when (it.itemId) {
          R.id.action_from -> viewModel?.setFromStop(stop)
          R.id.action_to -> viewModel?.setToStop(stop)
          R.id.action_show_routes -> {
            viewModel?.onShowTransportsClicked(stop)
          }
        }
        true
      }
    }
    .run { setOnClickListener { show() } }
}

fun TextView.setDisappearingError(error: Int) {
  alpha = 0f
  ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f).apply {
    doOnStart { setText(error) }
    duration = 2000
  }.start()
}

fun TextView.setSelectedStopName(stop: Stop, locale: String) {
  val value = stop.getCurrentName(locale)

  text = context.getString(R.string.label_selected_stop, value)
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