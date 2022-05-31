package robert.findtransport.presentation.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import robert.findtransport.presentation.compose.screens.home.HomeScreen

@Composable
fun Navigation() {
  val navController = rememberNavController()
  NavHost(
    navController = navController,
    startDestination = NavigationScreens.HomeScreen.name,
  ) {
    composable(route = NavigationScreens.HomeScreen.name) {
      HomeScreen(navController = navController)
    }
  }
}

sealed class NavigationScreens(val name: String) {
  object HomeScreen : NavigationScreens("home_screen")
}
