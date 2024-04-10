package robert.findtransport.presentation.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.navigation.NavController
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
//import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig
import robert.findtransport.data.model.enums.StopType
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.getMapStyle
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.map.components.BackButton
import robert.findtransport.presentation.screens.map.components.CentralPointer
import robert.findtransport.presentation.screens.map.components.CurrentLocationButton
import robert.findtransport.presentation.screens.map.components.FeedbackButton
import robert.findtransport.presentation.screens.map.components.SelectLocationButton
//import robert.findtransport.utils.extensions.enableLocationComponent
//import robert.findtransport.utils.extensions.flyTo
import robert.findtransport.utils.extensions.toLocation
import robert.findtransport.utils.extensions.toPoint

@OptIn(MapboxExperimental::class)
@Composable
fun LocationPickerContent(
  modifier: Modifier,
  locationEnabled: State<Boolean>,
  locationPickerViewModel: LocationPickerViewModel,
  homeViewModel: HomeViewModel,
  navController: NavController,
  pickerType: StopType,
) {
  val mapStyle = getMapStyle()
  val isMapMoving = rememberSaveable { mutableStateOf(false) }
  val centralPoint = rememberSaveable { mutableStateOf<Point?>(null) }

  Box(modifier = modifier.fillMaxSize()) {
    val scope = rememberCoroutineScope()

//    AndroidView(
//      modifier = Modifier.fillMaxSize(),
//      factory = { context ->
//        ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)
//        MapView(context = context)
//      },
//      update = { mapView ->
//        val map = mapView.getMapboxMap()
//
//        map.addOnMoveListener(object : OnMoveListener {
//          override fun onMove(detector: MoveGestureDetector): Boolean = false
//
//          override fun onMoveBegin(detector: MoveGestureDetector) {
//            isMapMoving.value = true
//          }
//
//          override fun onMoveEnd(detector: MoveGestureDetector) {
//            isMapMoving.value = false
//            centralPoint.value = map.cameraState.center
//          }
//        })
//
//        map.loadStyleUri(mapStyle) {
//          if (locationEnabled.value) {
//            mapView.enableLocationComponent()
//            locationPickerViewModel.getCurrentLocation()
//          }
//
//          scope.launch {
//            locationPickerViewModel.currentLocation.collectLatest { address ->
//              val currentLocation = address?.toLocation() ?: return@collectLatest
//              map.flyTo(currentLocation)
//              centralPoint.value = currentLocation.toPoint()
//            }
//          }
//          scope.launch {
//            locationPickerViewModel.centralPointStop.collectLatest { stop ->
//              val currentLocation =
//                stop?.coordinates?.firstOrNull()?.toLocation() ?: return@collectLatest
//              map.flyTo(currentLocation)
//              centralPoint.value = currentLocation.toPoint()
//            }
//          }
//
//          map.getFreeCameraOptions().position
//        }
//        NoOpUpdate
//      },
//    )

    CentralPointer(isMapMoving = isMapMoving)
    BackButton { navController.popBackStack() }
    FeedbackButton(modifier = Modifier.align(Alignment.TopEnd)) {
      navController.navigate(NavigationScreens.FeedbackScreen.name)
    }

    SelectLocationButton(
      pickerType = pickerType,
      locationEnabled = locationEnabled,
      centralPoint = centralPoint,
      homeViewModel = homeViewModel,
      navController = navController,
      locationPickerViewModel = locationPickerViewModel,
    )

    if (locationEnabled.value) {
      CurrentLocationButton { locationPickerViewModel.getCurrentLocation() }
    }
  }
}