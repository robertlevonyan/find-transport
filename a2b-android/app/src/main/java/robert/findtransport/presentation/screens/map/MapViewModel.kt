package robert.findtransport.presentation.screens.map

import android.Manifest
import android.location.Location
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU
import javax.inject.Inject

@HiltViewModel
open class MapViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  permissionUseCase: PermissionUseCase,
  private val locationUseCase: LocationUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val locationEnabled = MutableStateFlow(permissionUseCase.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)).asStateFlow()

  val currentLocation = MutableStateFlow(Location("default").apply {
    latitude = DEFAULT_LATITUDE
    longitude = DEFAULT_LONGITUDE
  })

  fun getCurrentLocation() {
    viewModelScope.launch(Dispatchers.Main) {
      currentLocation.value = locationUseCase.getCurrentLocation()
    }
  }

  fun getStopName(stop: Stop): String = when (locale.value) {
    LNG_EN -> stop.nameEn
    LNG_RU -> stop.nameRu
    else -> stop.nameAm
  }
}
