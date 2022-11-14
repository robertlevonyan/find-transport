package robert.findtransport.presentation.screens.map

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.isAppInDarkMode
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

  Box(modifier = modifier.fillMaxSize()) {
    when (mapType) {
      MapType.CHOOSER -> ChooserMapScreen(
        mapStyle = mapStyle,
        locationEnabled = locationEnabled,
        navController = navController,
        homeViewModel = homeViewModel,
      )
      MapType.PREVIEW -> PreviewMapScreen(
        mapStyle = mapStyle,
        locationEnabled = locationEnabled,
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
