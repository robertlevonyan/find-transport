package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.screens.transport.components.MapView

@Composable
fun MapContent(
  modifier: Modifier,
  navController: NavController,
  locale: String,
  locationEnabled: Boolean,
  mapStyle: String,
  transport: Transport,
  transportViewModel: TransportViewModel,
) {
  Box(modifier = modifier) {
    MapView(
      locale = locale,
      locationEnabled = locationEnabled,
      mapStyle = mapStyle,
      transport = transport,
      transportViewModel = transportViewModel
    )

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
}
