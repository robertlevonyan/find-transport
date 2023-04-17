package robert.findtransport.presentation.screens.home

import android.Manifest
import android.location.Address
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopWithAddress
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.IntroUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.domain.usecase.rate.RateUseCase
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getFormattedAddress
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  introUseCase: IntroUseCase,
  themeUseCase: ThemeUseCase,
  permissionUseCase: PermissionUseCase,
  private val locationUseCase: LocationUseCase,
  private val rateUseCase: RateUseCase,
) : BaseViewModel() {
  val introPassed = MutableStateFlow(introUseCase.isIntroPassed).asStateFlow()
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val theme = MutableStateFlow(themeUseCase.getTheme()).asStateFlow()
  val origin = MutableStateFlow<Address?>(null)
  val originLabel = MutableStateFlow<String?>(null)
  val originStop = MutableStateFlow<Stop?>(null)
  val destination = MutableStateFlow<Address?>(null)
  val destinationLabel = MutableStateFlow<String?>(null)
  val destinationStop = MutableStateFlow<Stop?>(null)
  val showRate = MutableStateFlow(rateUseCase.showDialog())
  private val locationEnabledChannel = Channel<Boolean>()
  val locationEnabled: Flow<Boolean> get() = locationEnabledChannel.consumeAsFlow()

  init {
    viewModelScope.launch {
      locationEnabledChannel.send(permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
    }
    rateUseCase.updateInterval()
  }

  fun getCurrentLocation() {
    viewModelScope.launch {
      val currentLocation = locationUseCase.getCurrentLocation() ?: return@launch
      setOrigin(
        latitude = currentLocation.latitude,
        longitude = currentLocation.longitude,
      )
    }
  }

  fun setOrigin(latitude: Double?, longitude: Double?, defaultStop: Stop? = null) {
    viewModelScope.launch {
      locationUseCase.getAddress(latitude, longitude)?.let { originAddress ->
        origin.value = originAddress
        originLabel.value = originAddress.getFormattedAddress(locale = locale.value)
      } ?: run {
        val nearbyStop = defaultStop ?: locationUseCase.getNearbyStop(
          latitude = latitude ?: return@launch,
          longitude = longitude ?: return@launch,
        )
        originStop.value = nearbyStop
        originLabel.value = nearbyStop?.getCurrentName(locale = locale.value)
      }
    }
  }

  fun setOriginStop(stopWithAddress: StopWithAddress?) {
    if (stopWithAddress?.address == null) return

    origin.value = stopWithAddress.address
    originLabel.value = stopWithAddress.stop.getCurrentName(locale.value)
    originStop.value = stopWithAddress.stop
  }

  fun setDestination(latitude: Double?, longitude: Double?, defaultStop: Stop? = null) {
    viewModelScope.launch {
      locationUseCase.getAddress(latitude, longitude)?.let { destinationAddress ->
        destination.value = destinationAddress
        destinationLabel.value = destinationAddress.getFormattedAddress(locale = locale.value)
      } ?: run {
        val nearbyStop = defaultStop ?: locationUseCase.getNearbyStop(
          latitude = latitude ?: return@launch,
          longitude = longitude ?: return@launch,
        )
        destinationStop.value = nearbyStop
        destinationLabel.value = nearbyStop?.getCurrentName(locale = locale.value)
      }
    }
  }

  fun setDestinationStop(stopWithAddress: StopWithAddress?) {
    if (stopWithAddress?.address == null) return
    viewModelScope.launch {
      destination.value = stopWithAddress.address
      destinationLabel.value = stopWithAddress.stop.getCurrentName(locale.value)
      destinationStop.value = stopWithAddress.stop
    }
  }

  fun swap() {
    val selectedOriginLabel = originLabel.value
    val selectedDestinationLabel = destinationLabel.value
    originLabel.value = selectedDestinationLabel
    destinationLabel.value = selectedOriginLabel

    val selectedOrigin = origin.value
    val selectedDestination = destination.value
    origin.value = selectedDestination
    destination.value = selectedOrigin

    val selectedOriginStop = originStop.value
    val selectedDestinationStop = destinationStop.value
    originStop.value = selectedDestinationStop
    destinationStop.value = selectedOriginStop
  }

  fun openRate() {
    viewModelScope.launch {
      rateUseCase.setRate()
      showRate.value = false
    }
  }

  fun dismissRate() {
    showRate.value = false
  }
}
