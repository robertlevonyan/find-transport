package robert.findtransport.presentation.screens.passingroutes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.A2bAppBar
import robert.findtransport.utils.EMPTY_ID

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
                hasFeedbackButton = true,
                navigationIcon = R.drawable.ic_arrow_back,
                onNavigationIconClick = { navController.popBackStack() },
                onFeedbackClick = { navController.navigate(NavigationScreens.FeedbackScreen) },
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
