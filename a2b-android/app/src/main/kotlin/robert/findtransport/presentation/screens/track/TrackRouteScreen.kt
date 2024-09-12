package robert.findtransport.presentation.screens.track

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun TrackRouteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    trackRouteViewModel: TrackRouteViewModel = hiltViewModel(),
    transportId: Int,
    fromId: Int,
    toId: Int,
) {
    trackRouteViewModel.initData(transportId, fromId, toId)

    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        TrackRouteContent(
            modifier = Modifier.padding(contentPadding),
            navController = navController,
            trackRouteViewModel = trackRouteViewModel,
        )
    }
}
