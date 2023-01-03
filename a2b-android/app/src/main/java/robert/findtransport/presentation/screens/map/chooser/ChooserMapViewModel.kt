package robert.findtransport.presentation.screens.map.chooser

import androidx.lifecycle.viewModelScope
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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

  init {
    viewModelScope.launch {
      try {
        if (!coroutineContext.isActive) return@launch
        val stops = async { stopsUseCase.getStopsLocations() }
        val metroStops = async { stopsUseCase.getMetroStopsLocations() }
        _allStops.value = stops.await() + metroStops.await()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }
}