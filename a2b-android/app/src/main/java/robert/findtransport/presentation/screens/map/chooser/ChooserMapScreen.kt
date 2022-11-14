package robert.findtransport.presentation.screens.map.chooser

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.JsonElement
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.entity.Stop
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.map.enableLocationComponent
import robert.findtransport.presentation.screens.map.flyTo
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun BoxScope.ChooserMapScreen(
  mapStyle: String,
  locationEnabled: Boolean,
  mapViewModel: ChooserMapViewModel = hiltViewModel(),
  navController: NavController,
  homeViewModel: HomeViewModel,
) {
  val scope = rememberCoroutineScope()
  val locale by mapViewModel.locale.collectAsState()
  var loading by remember { mutableStateOf(true) }
  var showStopOptions by remember { mutableStateOf<JsonElement?>(null) }

  AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
    ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)
    MapView(context = context)
  }, update = { mapView ->
    val map = mapView.getMapboxMap()

    val pointAnnotationManager = mapView.annotations.createPointAnnotationManager().apply {
      addClickListener(OnPointAnnotationClickListener { pointAnnotation ->
        pointAnnotation.getData()?.let { data -> showStopOptions = data }
        true
      })
    }

    map.loadStyleUri(mapStyle) {
      if (locationEnabled) {
        mapView.enableLocationComponent()
        mapViewModel.getCurrentLocation()
      }

      scope.launch {
        mapViewModel.currentLocation.collectLatest { currentLocation ->
          map.flyTo(currentLocation)
        }
      }
      scope.launch { mapViewModel.metroStops.collectLatest(pointAnnotationManager::create) }
      scope.launch {
        mapViewModel.allStops.collectLatest { allStops ->
          pointAnnotationManager.create(allStops)
          loading = false
        }
      }
    }
  })

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

  if (loading) {
    Box(
      modifier = Modifier
        .background(color = Color.Black.copy(alpha = 0.3f))
        .fillMaxSize()
    ) {
      CircularProgressIndicator(
        modifier = Modifier
          .wrapContentSize()
          .padding(FabPadding)
          .align(Alignment.Center),
        color = MaterialTheme.colorScheme.secondary,
      )
    }
  }

  showStopOptions?.let { options ->
    val stop = options.fromJson<Stop>().toStop()
    Dialog(onDismissRequest = { showStopOptions = null }) {
      Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = Shapes.medium,
      ) {
        LazyColumn(content = {
          item { TextPrimary(text = stop.getCurrentName(locale)) }
          item { Divider(modifier = Modifier.padding(vertical = HalfPadding)) }
          item {
            ModalItem(
              image = R.drawable.ic_chooser_from,
              label = R.string.action_set_from,
            ) {
              homeViewModel.setFromStop(stop)
              navController.popBackStack()
            }
          }
          item {
            ModalItem(
              image = R.drawable.ic_chooser_to,
              label = R.string.action_set_to,
            ) {
              homeViewModel.setToStop(stop)
              navController.popBackStack()
            }
          }
          item {
            ModalItem(
              image = R.drawable.ic_road,
              label = R.string.action_show,
            ) {
              navController.navigate(route = "${NavigationScreens.PassingRoutesScreen.name}/${stop.id}")
            }
          }
        })
      }
    }
  }
}

@Composable
private fun ModalItem(image: Int, label: Int, action: () -> Unit) {
  Row(modifier = Modifier
    .fillMaxWidth()
    .wrapContentHeight()
    .clickable { action.invoke() }) {
    Image(
      modifier = Modifier
        .padding(horizontal = FabPadding)
        .align(Alignment.CenterVertically),
      painter = painterResource(id = image),
      contentDescription = null,
    )
    TextSecondary(
      modifier = Modifier
        .wrapContentSize()
        .align(Alignment.CenterVertically),
      text = stringResource(id = label),
      textAlign = TextAlign.Start,
    )
  }
}
