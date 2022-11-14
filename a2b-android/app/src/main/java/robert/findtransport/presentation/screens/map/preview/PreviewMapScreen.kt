package robert.findtransport.presentation.screens.map.preview

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptionsManager
import robert.findtransport.BuildConfig
import robert.findtransport.presentation.screens.map.enableLocationComponent

@Composable
fun BoxScope.PreviewMapScreen(
  mapStyle: String,
  locationEnabled: Boolean,
  mapViewModel: PreviewMapViewModel = hiltViewModel(),
) {
  AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = { context ->
      ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)

      MapView(context = context).apply {
        val map = getMapboxMap()
        map.loadStyleUri(
          styleUri = mapStyle,
          onStyleLoaded = { style ->
            if (locationEnabled) {
              enableLocationComponent()
            }

          }
        )
      }
    }
  )
}