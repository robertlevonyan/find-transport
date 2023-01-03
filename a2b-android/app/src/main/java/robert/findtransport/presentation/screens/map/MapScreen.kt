package robert.findtransport.presentation.screens.map

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.location
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.model.enums.MapType
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.map.chooser.ChooserMapScreen
import robert.findtransport.presentation.screens.map.preview.PreviewMapScreen
import robert.findtransport.utils.EMPTY_ID
import robert.findtransport.utils.extensions.getBitmapFromVectorDrawable
import robert.findtransport.utils.extensions.getColorFromRes

@Composable
fun MapScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  mapViewModel: MapViewModel = hiltViewModel(),
  homeViewModel: HomeViewModel,
  mapType: MapType,
  transportId: Int = EMPTY_ID,
  underground: Boolean = false,
  reversed: Boolean = false,
) {
  val mapStyle = getMapStyle()
  val locationEnabled by mapViewModel.locationEnabled.collectAsState()
  var showPermissionDialog by rememberSaveable { mutableStateOf(!locationEnabled) }

  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    mapViewModel.setLocationEnabled(isGranted)
  }

  if (showPermissionDialog) {
    Dialog(onDismissRequest = {
      showPermissionDialog = false
    }) {
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
            onClick = {
              launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
              showPermissionDialog = false
            },
            shape = RectangleShape
          ) {
            Text(
              text = stringResource(id = R.string.permission_yes),
              fontWeight = FontWeight.Bold,
            )
          }
          Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surface,
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            onClick = {
              showPermissionDialog = false
            },
            shape = RectangleShape,
          ) {
            Text(text = stringResource(id = R.string.permission_no), textAlign = TextAlign.Center)
          }
        }
      }
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    when (mapType) {
      MapType.CHOOSER -> ChooserMapScreen(
        mapStyle = mapStyle,
        locationEnabled = locationEnabled,
        navController = navController,
        homeViewModel = homeViewModel,
        mapViewModel = mapViewModel,
      )
      MapType.PREVIEW -> PreviewMapScreen(
        mapStyle = mapStyle,
        locationEnabled = locationEnabled,
        mapViewModel = mapViewModel,
        navController = navController,
        transportId = transportId,
        underground = underground,
        reversed = reversed,
      )
      MapType.SEARCH -> return
    }

    SmallFloatingActionButton(
      modifier = Modifier.padding(FabPadding),
      containerColor = androidx.compose.ui.graphics.Color.Transparent,
      onClick = { navController.popBackStack() }) {
      Icon(
        painter = painterResource(id = R.drawable.ic_arrow_back),
        contentDescription = stringResource(id = R.string.cd_current_location),
      )
    }

    if (locationEnabled) {
      FloatingActionButton(modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(FabPadding),
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = Black,
        onClick = {
          mapViewModel.getCurrentLocation()
        }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_current_location_default),
          contentDescription = stringResource(id = R.string.cd_current_location),
          tint = Black,
        )
      }
    }
  }
}

@Composable
private fun getMapStyle(): String = if (isAppInDarkMode()) {
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
      topImage = BitmapDrawable(resources, context?.getBitmapFromVectorDrawable(R.drawable.ic_bearing))
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
