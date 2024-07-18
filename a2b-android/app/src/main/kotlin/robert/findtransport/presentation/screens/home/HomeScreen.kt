package robert.findtransport.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
) {
    Scaffold { contentPadding ->
        Box(Modifier.padding(contentPadding)) {
            HomeContent(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                homeViewModel = homeViewModel,
            )
        }
    }
}
