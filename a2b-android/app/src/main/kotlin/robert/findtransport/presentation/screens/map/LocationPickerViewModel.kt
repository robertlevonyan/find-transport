package robert.findtransport.presentation.screens.map

import android.Manifest
import android.location.Address
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import javax.inject.Inject

@HiltViewModel
open class LocationPickerViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  permissionUseCase: PermissionUseCase,
  private val locationUseCase: LocationUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  private val _locationEnabled =
    MutableStateFlow(permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
  val locationEnabled get() = _locationEnabled.asStateFlow()

  val currentLocation = MutableSharedFlow<Address?>()
  val centralPointAddress = MutableSharedFlow<Address?>()
  val centralPointStop = MutableSharedFlow<Stop?>()

  fun setLocationEnabled(enabled: Boolean) {
    _locationEnabled.value = enabled
  }

  fun getCurrentLocation() {
    viewModelScope.launch {
      val currentLocationAddress = locationUseCase.getCurrentLocation()
      currentLocationAddress?.takeIf { it.thoroughfare != null && it.featureName != null }
        ?.let { currentLocation.emit(it) }
        ?: run {
          if (currentLocationAddress == null) return@launch

          val stop = locationUseCase.getNearbyStop(
            latitude = currentLocationAddress.latitude,
            longitude = currentLocationAddress.longitude,
          )

          centralPointStop.emit(stop)
        }
    }
  }

  fun getAddress(point: Point) {
    viewModelScope.launch {
      locationUseCase.getAddress(
        latitude = point.latitude(),
        longitude = point.longitude(),
      )
        ?.let { centralPointAddress.emit(it) }
        ?: run {
          centralPointStop.emit(
            locationUseCase.getNearbyStop(
              latitude = point.latitude(),
              longitude = point.longitude(),
            )
          )
        }
    }
  }
}
