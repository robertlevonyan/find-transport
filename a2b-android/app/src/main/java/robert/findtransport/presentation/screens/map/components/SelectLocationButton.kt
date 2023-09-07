package robert.findtransport.presentation.screens.map.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.enums.StopType
import robert.findtransport.presentation.reusables.BlackVariant
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.SmallFabSize
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.map.LocationPickerViewModel
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getFormattedAddress

@Composable
fun BoxScope.SelectLocationButton(
  pickerType: StopType,
  locationEnabled: State<Boolean>,
  centralPoint: MutableState<Point?>,
  homeViewModel: HomeViewModel,
  navController: NavController,
  locationPickerViewModel: LocationPickerViewModel,
) {
  var buttonText by rememberSaveable { mutableStateOf("") }
  val locale by homeViewModel.locale.collectAsState()
  val centralStop by locationPickerViewModel.centralPointStop.collectAsState(null)
  centralPoint.value?.let(locationPickerViewModel::getAddress)

  val centralAddress by locationPickerViewModel.centralPointAddress.collectAsState(null)
  val formattedAddress = centralAddress?.getFormattedAddress(locale = locale)
    ?: centralStop?.getCurrentName(locale = locale).orEmpty()

  buttonText = when (pickerType) {
    StopType.ORIGIN -> stringResource(id = R.string.label_select_origin, formattedAddress)
    StopType.DESTINATION -> stringResource(id = R.string.label_select_destination, formattedAddress)
  }

  Button(onClick = {
    when (pickerType) {
      StopType.ORIGIN -> homeViewModel.setOrigin(
        latitude = centralAddress?.latitude,
        longitude = centralAddress?.longitude,
        defaultStop = centralStop,
      )
      StopType.DESTINATION -> homeViewModel.setDestination(
        latitude = centralAddress?.latitude,
        longitude = centralAddress?.longitude,
        defaultStop = centralStop,
      )
    }
    navController.popBackStack()
  },
    modifier = Modifier
      .align(Alignment.BottomCenter)
      .fillMaxWidth()
      .padding(start = FabPadding)
      .padding(vertical = FabPadding)
      .run {
        val paddingAddition = if (locationEnabled.value) SmallFabSize + FabPadding else 0.dp
        padding(end = paddingAddition + FabPadding)
      }
      .height(SmallFabSize),
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondary, contentColor = BlackVariant
    )) {
    Text(
      text = buttonText,
      fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
    )
  }
}
