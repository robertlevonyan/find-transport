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
                navController.navigate(
                    route = NavigationScreens.SearchScreen(
                        originName = historyItem.originName,
                        originLatitude = historyItem.originLatitude,
                        originLongitude = historyItem.originLongitude,
                        originStopId = historyItem.fromStop.id,
                        destinationName = historyItem.destinationName,
                        destinationLatitude = historyItem.destinationLatitude,
                        destinationLongitude = historyItem.destinationLongitude,
                        destinationStopId = historyItem.toStop.id,
                        opened = SearchOpenInitiator.HOME.name,
                    )
                )
            },
            onRemoveHistoryClicked = historyViewModel::removeItem,
            onClearHistoryClicked = historyViewModel::clearHistory
        )
    }
}
