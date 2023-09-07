package robert.findtransport.presentation.screens.transports

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import robert.findtransport.R
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.A2bAppBar

@Composable
fun TransportsScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportsViewModel: TransportsViewModel = hiltViewModel(),
) {
  val locale by transportsViewModel.locale.collectAsState()
  var transportCategory by rememberSaveable { mutableStateOf(TransportCategory.BUS) }

  Scaffold(modifier = modifier, topBar = {
    A2bAppBar(
      title = stringResource(id = R.string.title_transports),
      hasFeedbackButton = true,
      navigationIcon = R.drawable.ic_arrow_back,
      onNavigationIconClick = { navController.popBackStack() },
      onFeedbackClick = { navController.navigate(NavigationScreens.FeedbackScreen.name) },
    )
  }) { contentPadding ->
    val transports = when (transportCategory) {
      TransportCategory.BUS -> transportsViewModel.buses
      TransportCategory.MICROBUS -> transportsViewModel.microbuses
      TransportCategory.TROLLEYBUS -> transportsViewModel.trolleybuses
      TransportCategory.METRO -> transportsViewModel.metro
    }.collectAsLazyPagingItems()

    TransportsContent(
      modifier = Modifier
        .padding(contentPadding),
      transports = transports,
      locale = locale,
      transportCategory = transportCategory,
      onTransportCategoryClick = { transportCategory = it },
      onTransportClick = { transport ->
        navController.navigate(
          route = NavigationScreens.TransportScreen.name + "?transport_id=${transport.id}" + "&show_options=${true}"
        ) {
          navController.graph.route?.let { route ->
            popUpTo(route) { saveState = true }
          }
          launchSingleTop = true
          restoreState = true
        }
      })
  }
}
