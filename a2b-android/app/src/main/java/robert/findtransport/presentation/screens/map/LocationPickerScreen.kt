package robert.findtransport.presentation.screens.map

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.utils.internal.toPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.model.enums.StopType
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.utils.*
import robert.findtransport.utils.extensions.getBitmapFromVectorDrawable
import robert.findtransport.utils.extensions.getColorFromRes
import robert.findtransport.utils.extensions.getFormattedAddress
import robert.findtransport.utils.extensions.toLocation
import java.util.*

@Composable
fun LocationPickerScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  locationPickerViewModel: LocationPickerViewModel = hiltViewModel(),
  homeViewModel: HomeViewModel,
  pickerType: StopType,
) {
  val mapStyle = getMapStyle()
  val locationEnabled = locationPickerViewModel.locationEnabled.collectAsState()
  var showPermissionDialog by rememberSaveable { mutableStateOf(!locationEnabled.value) }
  val isMapMoving = rememberSaveable { mutableStateOf(false) }
  val centralPoint =
    rememberSaveable { mutableStateOf(Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)) }

  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    locationPickerViewModel.setLocationEnabled(isGranted)
  }

  if (showPermissionDialog) {
    PermissionDialog(
      modifier = modifier,
      onDismiss = { showPermissionDialog = false },
      onGrant = {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        showPermissionDialog = false
      },
      onDecline = { showPermissionDialog = false })
  }

  Box(modifier = modifier.fillMaxSize()) {
    val scope = rememberCoroutineScope()

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
      ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)
      MapView(context = context)
    }, update = { mapView ->
      val map = mapView.getMapboxMap()

      map.addOnMoveListener(object : OnMoveListener {
        override fun onMove(detector: MoveGestureDetector): Boolean = false

        override fun onMoveBegin(detector: MoveGestureDetector) {
          isMapMoving.value = true
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
          isMapMoving.value = false
          centralPoint.value = map.cameraState.center
        }
      })

      map.loadStyleUri(mapStyle) {
        if (locationEnabled.value) {
          mapView.enableLocationComponent()
          locationPickerViewModel.getCurrentLocation()
        }

        scope.launch {
          locationPickerViewModel.currentLocation.collectLatest { address ->
            val currentLocation = address?.toLocation() ?: return@collectLatest
            map.flyTo(currentLocation)
            centralPoint.value = currentLocation.toPoint()
          }
        }

        map.getFreeCameraOptions().position
      }
    })

    CentralPointer(isMapMoving = isMapMoving)

    BackButton(navController = navController)

    SelectLocationButton(
      pickerType = pickerType,
      locationEnabled = locationEnabled,
      centralPoint = centralPoint,
      homeViewModel = homeViewModel,
      navController = navController
    )

    if (locationEnabled.value) {
      CurrentLocationButton(locationPickerViewModel = locationPickerViewModel)
    }
  }
}

@Composable
fun BoxScope.SelectLocationButton(
  pickerType: StopType,
  locationEnabled: State<Boolean>,
  centralPoint: MutableState<Point>,
  homeViewModel: HomeViewModel,
  navController: NavController,
) {
  var buttonText by rememberSaveable { mutableStateOf("") }
  val locale by homeViewModel.locale.collectAsState()

  val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
  val point = centralPoint.value
  val address = geocoder.getFromLocation(point.latitude(), point.longitude(), 1)
    ?.firstOrNull()
  val formattedAddress = address?.getFormattedAddress(locale = locale).orEmpty()

  buttonText = when (pickerType) {
    StopType.ORIGIN -> stringResource(id = R.string.label_select_origin, formattedAddress)
    StopType.DESTINATION -> stringResource(id = R.string.label_select_destination, formattedAddress)
  }

  Button(
    onClick = {
      when (pickerType) {
        StopType.ORIGIN -> homeViewModel.setOrigin(address = address)
        StopType.DESTINATION -> homeViewModel.setDestination(address = address)
      }
      navController.popBackStack()
    },
    modifier = Modifier
      .align(Alignment.BottomCenter)
      .fillMaxWidth()
      .padding(start = FabPadding)
      .padding(vertical = FabPadding)
      .run {
        val paddingAddition = if (locationEnabled.value) {
          SmallFabSize + FabPadding
        } else {
          0.dp
        }
        padding(end = paddingAddition + FabPadding)
      }
      .height(SmallFabSize),
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondary,
      contentColor = BlackVariant
    )
  ) {
    Text(
      text = buttonText,
      fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
    )
  }
}

//fun getAddress(address: Address?, locale: StateFlow<String>): String {
//  address ?: return ""
//  return when (locale.value) {
//    LNG_AM -> "${address.thoroughfare} ${address.featureName}"
//    LNG_EN -> "${address.featureName} ${address.thoroughfare}"
//    LNG_RU -> "${address.thoroughfare} ${address.featureName}"
//    else -> "${address.featureName} ${address.thoroughfare}"
//  }
//}

@Composable
fun PermissionDialog(
  modifier: Modifier,
  onDismiss: () -> Unit,
  onGrant: () -> Unit,
  onDecline: () -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .padding(horizontal = FabPadding)
        .padding(bottom = FabPadding)
        .fillMaxWidth()
        .wrapContentSize(),
      shape = Shapes.medium,
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
      Column(modifier) {
        TextPrimary(
          modifier = Modifier
            .padding(FabPadding)
            .align(Alignment.CenterHorizontally),
          text = stringResource(id = R.string.permission_title)
        )
        Image(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          painter = painterResource(id = R.drawable.il_location_access),
          contentDescription = stringResource(id = R.string.permission_title)
        )
        TextSecondary(
          modifier = Modifier
            .padding(FabPadding)
            .align(Alignment.CenterHorizontally),
          text = stringResource(id = R.string.permission_message),
          textAlign = TextAlign.Start,
        )
        Button(
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(
            containerColor = Accent,
            contentColor = Black,
          ),
          onClick = onGrant,
          shape = RectangleShape
        ) {
          Text(
            text = stringResource(id = R.string.permission_yes),
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
          )
        }
        Button(
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
          onClick = onDecline,
          shape = RectangleShape,
        ) {
          Text(
            text = stringResource(id = R.string.permission_no),
            textAlign = TextAlign.Center,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
          )
        }
      }
    }
  }

}

@Composable
fun BoxScope.CentralPointer(isMapMoving: State<Boolean>) {
  val icon = if (isMapMoving.value) {
    R.drawable.ic_origin
  } else {
    R.drawable.ic_origin_idle
  }
  Image(
    modifier = Modifier
      .wrapContentSize()
      .align(Alignment.Center)
      .animateContentSize(),
    painter = painterResource(id = icon),
    contentDescription = null,
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
  )
}

@Composable
fun BackButton(navController: NavController) {
  SmallFloatingActionButton(modifier = Modifier.padding(
    vertical = FabPadding, horizontal = HalfPadding
  ),
    containerColor = MaterialTheme.colorScheme.secondary,
    onClick = { navController.popBackStack() }) {
    Icon(
      painter = painterResource(id = R.drawable.ic_arrow_back),
      contentDescription = stringResource(id = R.string.cd_current_location),
    )
  }
}

@Composable
fun BoxScope.CurrentLocationButton(locationPickerViewModel: LocationPickerViewModel) {
  FloatingActionButton(modifier = Modifier
    .align(Alignment.BottomEnd)
    .padding(FabPadding),
    containerColor = MaterialTheme.colorScheme.secondary,
    contentColor = Black,
    onClick = {
      locationPickerViewModel.getCurrentLocation()
    }) {
    Icon(
      painter = painterResource(id = R.drawable.ic_current_location),
      contentDescription = stringResource(id = R.string.cd_current_location),
      tint = Black,
    )
  }
}

@Composable
internal fun getMapStyle(): String = if (isAppInDarkMode()) {
  BuildConfig.MAPBOX_STYLE_NIGHT
} else {
  BuildConfig.MAPBOX_STYLE_LIGHT
}

internal fun MapView.enableLocationComponent() {
  location.updateSettings {
    enabled = true
    pulsingEnabled = false
    pulsingColor = context?.getColorFromRes(R.color.colorAccent300) ?: Color.YELLOW
    locationPuck = LocationPuck2D().apply {
      topImage =
        BitmapDrawable(resources, context?.getBitmapFromVectorDrawable(R.drawable.ic_bearing))
    }
  }
}

internal fun MapboxMap.flyTo(location: Location) {
  try {
    flyTo(
      cameraOptions = CameraOptions.Builder()
        .center(Point.fromLngLat(location.longitude, location.latitude))
        .zoom(15.0)
        .build(),
      animationOptions = MapAnimationOptions.mapAnimationOptions {
        duration(duration = 200)
        interpolator(interpolator = FastOutSlowInInterpolator())
      },
    )
  } catch (e: Exception) {
    e.printStackTrace()
  }
}
