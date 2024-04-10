package robert.findtransport.presentation.screens.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.DisposableMapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.gestures.removeOnMoveListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import robert.findtransport.data.model.enums.StopType
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.getMapStyle
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.picker.components.BackButton
import robert.findtransport.presentation.screens.picker.components.CentralPointer
import robert.findtransport.presentation.screens.picker.components.CurrentLocationButton
import robert.findtransport.presentation.screens.picker.components.FeedbackButton
import robert.findtransport.presentation.screens.picker.components.SelectLocationButton
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.extensions.getLocationComponent

@OptIn(MapboxExperimental::class)
@Composable
fun LocationPickerContent(
    modifier: Modifier,
    locationPickerViewModel: LocationPickerViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController,
    pickerType: StopType,
) {
    val mapStyle = getMapStyle()
    val isMapMoving = rememberSaveable { mutableStateOf(false) }
    val locationEnabled by locationPickerViewModel.locationEnabled.collectAsState()
    val centralPoint by locationPickerViewModel.centralPointStop.collectAsState(initial = null)
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(13.0)
            center(Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE))
            pitch(0.0)
            bearing(0.0)
        }
    }

    LaunchedEffect(Unit) {
        locationPickerViewModel.locationEnabled.onEach { locationEnabled ->
            if (locationEnabled) {
                locationPickerViewModel.getCurrentLocation()
            }
        }.launchIn(this)
        locationPickerViewModel.currentLocation.onEach { currentLocationAddress ->
            currentLocationAddress?.let { point ->
                mapViewportState.flyTo(
                    cameraOptions = CameraOptions.Builder()
                        .center(Point.fromLngLat(point.longitude, point.latitude))
                        .zoom(15.0).build(),
                    animationOptions = MapAnimationOptions.mapAnimationOptions {
                        duration(duration = 200)
                        interpolator(interpolator = FastOutSlowInInterpolator())
                    },
                )
            }
        }.launchIn(this)
    }

    Box(modifier = modifier.fillMaxSize()) {
        val context = LocalContext.current
        val locationComponentSettings = getLocationComponent(context, locationEnabled)

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapInitOptionsFactory = { ctx ->
                MapInitOptions(
                    context = ctx,
                    styleUri = mapStyle,
                )
            },
            mapViewportState = mapViewportState,
            locationComponentSettings = locationComponentSettings,
            content = {
                DisposableMapEffect(key1 = this) { mapView ->
                    val map = mapView.mapboxMap
                    val onMoveListener = object : OnMoveListener {
                        override fun onMove(detector: MoveGestureDetector): Boolean = false

                        override fun onMoveBegin(detector: MoveGestureDetector) {
                            isMapMoving.value = true
                        }

                        override fun onMoveEnd(detector: MoveGestureDetector) {
                            isMapMoving.value = false
                            locationPickerViewModel.setCentralPoint(map.cameraState.center)
                        }
                    }
                    map.addOnMoveListener(onMoveListener)
                    onDispose {
                        map.removeOnMoveListener(onMoveListener)
                    }
                }
            }
        )

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

        if (locationEnabled) {
            CurrentLocationButton { locationPickerViewModel.getCurrentLocation() }
        }
    }
}