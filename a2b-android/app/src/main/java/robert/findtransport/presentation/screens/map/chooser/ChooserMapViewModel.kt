package robert.findtransport.presentation.screens.map.chooser

import androidx.lifecycle.viewModelScope
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.presentation.screens.map.MapViewModel
import javax.inject.Inject

@HiltViewModel
class ChooserMapViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  permissionUseCase: PermissionUseCase,
  stopsUseCase: StopsUseCase,
  locationUseCase: LocationUseCase,
) : MapViewModel(localeUseCase, permissionUseCase, locationUseCase) {

  private val _allStops = MutableStateFlow(emptyList<PointAnnotationOptions>())
  val allStops: StateFlow<List<PointAnnotationOptions>> get() = _allStops.asStateFlow()

  private val _metroStops = MutableStateFlow(emptyList<PointAnnotationOptions>())
  val metroStops: StateFlow<List<PointAnnotationOptions>> get() = _metroStops.asStateFlow()

  init {
    viewModelScope.launch {
      try {
        if (!coroutineContext.isActive) return@launch
        val stops = stopsUseCase.getStopsLocations()
        _allStops.value = stops
        val metroStops = stopsUseCase.getMetroStopsLocations()
        _metroStops.value = metroStops
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }
}