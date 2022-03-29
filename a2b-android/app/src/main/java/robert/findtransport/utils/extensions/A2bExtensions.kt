package robert.findtransport.utils.extensions

import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.animation.doOnStart
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputLayout
import robert.findtransport.R
import robert.findtransport.data.model.*
import robert.findtransport.data.model.enums.LocationPermission
import robert.findtransport.data.model.enums.TransportType.*
import robert.findtransport.databinding.ItemSettingDropdownBinding
import robert.findtransport.databinding.ItemSettingProgressBinding
import robert.findtransport.databinding.ItemSettingSwitchBinding
import robert.findtransport.presentation.component.adapter.*
import robert.findtransport.presentation.detail.DetailViewModel
import robert.findtransport.presentation.history.HistoryViewModel
import robert.findtransport.utils.CustomTypefaceSpan
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

fun View.setCustomWidth(w: Float) = updateLayoutParams { width = w.toInt() }

fun TextSwitcher.setInitialText(text: String?) = setText(text)

fun FrameLayout.setEndView(type: SettingData.EndViewType): ViewBinding? {
  val binding = when (type) {
    SettingData.EndViewType.NONE -> return null
    SettingData.EndViewType.IMAGE -> ItemSettingDropdownBinding.inflate(LayoutInflater.from(context)/*, this@setEndView, true*/)
    SettingData.EndViewType.SWITCH -> ItemSettingSwitchBinding.inflate(LayoutInflater.from(context)/*, this@setEndView, true*/)
    SettingData.EndViewType.PROGRESS -> ItemSettingProgressBinding.inflate(LayoutInflater.from(context)/*, this@setEndView, true*/)
  }
  addView(binding.root)
  return binding
}

fun TextView.setBold(bold: Boolean?) {
  if (bold == null) return

  setTypeface(ResourcesCompat.getFont(context, R.font.app_font), if (bold) Typeface.BOLD else Typeface.NORMAL)
}

fun TextView.setFeedbackMessage() {
  SpannableStringBuilder(context.getString(R.string.message_feedback)).apply {
    ResourcesCompat.getFont(context, R.font.mdf)?.let {
      setSpan(CustomTypefaceSpan(it), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
      text = this
    }
  }
}

fun TextInputLayout.setCustomError(message: Int?) {
  error = context.getString(message ?: return)
  editText?.doAfterTextChanged { error = null }
}

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

fun View.setStopOptionsMenu(viewModel: DetailViewModel?, stop: Stop) {
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

fun View.setHistoryOptionsMenu(onClearAction: () -> Unit) {
  PopupMenu(context, this)
    .apply {
      menuInflater.inflate(R.menu.menu_history, menu)
      setOnMenuItemClickListener {
        when (it.itemId) {
          R.id.action_clear -> onClearAction.invoke()
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

fun TextView.setDate(timestamp: Long?, locale: String?) {
  timestamp?.run {
    text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag(locale ?: LNG_EN)).run {
      format(Date(timestamp))
    }
  }
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
