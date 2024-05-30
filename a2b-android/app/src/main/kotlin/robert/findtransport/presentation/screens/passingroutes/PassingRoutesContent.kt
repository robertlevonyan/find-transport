package robert.findtransport.presentation.screens.passingroutes

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.colorVariantInvertTransparent
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.reusables.composables.TransportListElement
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun PassingRoutesContent(
  modifier: Modifier,
  navController: NavController,
  passingRoutesViewModel: PassingRoutesViewModel,
) {
  val locale by passingRoutesViewModel.locale.collectAsState()
  val stop by passingRoutesViewModel.stop.collectAsState(initial = Stop.EMPTY)
  val transports by passingRoutesViewModel.transports.collectAsState()

  LazyColumn(modifier = modifier) {
    item {
      TextSecondary(
        modifier = Modifier.padding(FabPadding),
        text = stringResource(id = R.string.label_selected_stop, stop.getCurrentName(locale)),
        textAlign = TextAlign.Start,
      )
    }
    itemsIndexed(transports) { index, transport ->
      TransportListElement(
        transport = transport,
        locale = locale,
        onElementClick = {
          navController.navigate(
            route = NavigationScreens.TransportScreen.name +
                "?transport_id=${transport.id}" +
                "&show_options=${false}"
          )
        },
      )

      val thickness = if (index < transports.lastIndex) 0.5.dp else 0.dp
      Divider(
        color = colorVariantInvertTransparent(),
        thickness = thickness,
      )
    }
  }
}
