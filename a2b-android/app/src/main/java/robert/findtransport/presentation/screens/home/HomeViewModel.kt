package robert.findtransport.presentation.screens.home

import android.Manifest
import android.location.Address
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
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
  val destination = MutableStateFlow<Address?>(null)
  val destinationLabel = MutableStateFlow<String?>(null)
  val showRate = MutableStateFlow(rateUseCase.showDialog())
  val locationEnabled =
    MutableStateFlow(permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))

  init {
    rateUseCase.updateInterval()
  }

  fun getCurrentLocation() {
    if (origin.value != null) return

    viewModelScope.launch(Dispatchers.Main) {
      setOrigin(locationUseCase.getCurrentLocation())
    }
  }

  fun setOrigin(address: Address?) {
    origin.value = address
    originLabel.value = address?.getFormattedAddress(locale = locale.value)
  }

  fun setOriginStop(stopWithAddress: StopWithAddress?) {
    if (stopWithAddress?.address == null) return
    origin.value = stopWithAddress.address
    originLabel.value = stopWithAddress.stop.getCurrentName(locale.value)
  }

  fun setDestination(address: Address?) {
    destination.value = address
    destinationLabel.value = address?.getFormattedAddress(locale = locale.value)
  }

  fun setDestinationStop(stopWithAddress: StopWithAddress?) {
    if (stopWithAddress?.address == null) return
    destination.value = stopWithAddress.address
    destinationLabel.value = stopWithAddress.stop.getCurrentName(locale.value)
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
