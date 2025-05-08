package robert.findtransport.presentation.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.screens.home.components.HomeAppBar

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
) {
    Scaffold(
        topBar = {
            HomeAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                onHistoryButtonClicked = { navController.navigate(NavigationScreens.HistoryScreen) },
                onSettingsScreenClicked = { navController.navigate(NavigationScreens.SettingsScreen) },
                onFeedbackScreenClicked = { navController.navigate(NavigationScreens.FeedbackScreen) },
            )
        },
    ) { contentPadding ->
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            navController = navController,
            homeViewModel = homeViewModel,
        )
    }
}
