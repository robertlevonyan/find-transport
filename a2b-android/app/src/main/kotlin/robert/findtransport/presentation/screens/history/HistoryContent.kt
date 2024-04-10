package robert.findtransport.presentation.screens.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.screens.history.components.HistoryListScreen
import robert.findtransport.presentation.screens.history.components.NoHistoryScreen
import robert.findtransport.presentation.screens.search.SearchOpenInitiator

@Composable
fun HistoryContent(
  modifier: Modifier,
  navController: NavController,
  historyViewModel: HistoryViewModel,
) {
  val history by historyViewModel.allHistory.collectAsState()

  if (history.isEmpty()) {
    NoHistoryScreen(modifier)
  } else {
    HistoryListScreen(
      modifier = modifier,
      history = history,
      onRestoreHistoryClicked = { historyItem ->
        val navigationRoute = buildString {
          append("${NavigationScreens.SearchScreen.name}?")
          append("origin_name=${historyItem.originName}")
          append("&origin_latitude=${historyItem.originLatitude}")
          append("&origin_longitude=${historyItem.originLongitude}")
          append("&origin_stop_id=${historyItem.fromStop.id}")
          append("&destination_name=${historyItem.destinationName}")
          append("&destination_latitude=${historyItem.destinationLatitude}")
          append("&destination_longitude=${historyItem.destinationLongitude}")
          append("&destination_stop_id=${historyItem.toStop.id}")
          append("&opened=${SearchOpenInitiator.HOME.name}")
        }
        navController.navigate(route = navigationRoute)
      },
      onRemoveHistoryClicked = historyViewModel::removeItem,
      onClearHistoryClicked = historyViewModel::clearHistory
    )
  }
}
