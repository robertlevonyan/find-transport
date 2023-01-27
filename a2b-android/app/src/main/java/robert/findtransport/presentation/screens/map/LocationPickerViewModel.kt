package robert.findtransport.presentation.screens.map

import android.Manifest
import android.location.Location
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import javax.inject.Inject

@HiltViewModel
open class LocationPickerViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  permissionUseCase: PermissionUseCase,
  private val locationUseCase: LocationUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  private val _locationEnabled = MutableStateFlow(permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
  val locationEnabled get() = _locationEnabled.asStateFlow()

  val currentLocation = MutableStateFlow(Location("default").apply {
    latitude = DEFAULT_LATITUDE
    longitude = DEFAULT_LONGITUDE
  })

  fun setLocationEnabled(enabled: Boolean) {
    _locationEnabled.value = enabled
  }

  fun getCurrentLocation() {
    viewModelScope.launch(Dispatchers.Main) {
      currentLocation.value = locationUseCase.getCurrentLocation()
    }
  }
}
