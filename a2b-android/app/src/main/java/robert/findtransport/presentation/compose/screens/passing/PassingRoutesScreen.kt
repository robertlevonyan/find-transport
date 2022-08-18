package robert.findtransport.presentation.compose.screens.passing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.FabPadding
import robert.findtransport.presentation.compose.reusables.colorVariantInvertTransparent
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TextMessage
import robert.findtransport.presentation.compose.reusables.composables.TransportListElement
import robert.findtransport.utils.EMPTY_ID
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun PassingRoutesScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  stopId: Int,
  passingRoutesViewModel: PassingRoutesViewModel = hiltViewModel(),
) {
  if (stopId == EMPTY_ID) {
    navController.popBackStack()
    return
  }
  passingRoutesViewModel.getStopAndTransports(stopId)

  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_details),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
      )
    }
  ) { contentPadding ->
    PassingRoutesContent(
      modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      navController = navController,
      passingRoutesViewModel = passingRoutesViewModel,
    )
  }
}

@Composable
private fun PassingRoutesContent(
  modifier: Modifier,
  navController: NavController,
  passingRoutesViewModel: PassingRoutesViewModel,
) {
  val locale by passingRoutesViewModel.locale.collectAsState()
  val stop by passingRoutesViewModel.stop.collectAsState(initial = Stop.EMPTY)
  val transports by passingRoutesViewModel.transports.collectAsState()

  LazyColumn(modifier = modifier) {
    item {
      TextMessage(
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
          navController.navigate(route = "${NavigationScreens.TransportScreen.name}/${transport.id}")
        },
      )

      if (index < transports.lastIndex) {
        Divider(
          color = colorVariantInvertTransparent(),
          thickness = 0.5.dp,
        )
      }
    }
  }
}
