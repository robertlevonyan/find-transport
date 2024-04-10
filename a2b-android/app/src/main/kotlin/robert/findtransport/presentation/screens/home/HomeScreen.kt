package robert.findtransport.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
