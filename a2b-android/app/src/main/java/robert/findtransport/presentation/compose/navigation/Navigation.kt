package robert.findtransport.presentation.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import robert.findtransport.presentation.compose.screens.home.HomeScreen
import robert.findtransport.presentation.compose.screens.stops.StopsPickerScreen
import robert.findtransport.presentation.compose.screens.transports.TransportsScreen

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
    composable(route = NavigationScreens.TransportsScreen.name) {
      TransportsScreen(navController = navController)
    }
    composable(route = NavigationScreens.StopsPickerScreen.name) {
      StopsPickerScreen(navController = navController)
    }
  }
}

sealed class NavigationScreens(val name: String) {
  object HomeScreen : NavigationScreens("home_screen")
  object TransportsScreen : NavigationScreens("transports_screen")
  object StopsPickerScreen : NavigationScreens("stops_picker_screen")
}
