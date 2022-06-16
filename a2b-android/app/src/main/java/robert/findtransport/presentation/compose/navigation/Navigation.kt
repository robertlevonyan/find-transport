package robert.findtransport.presentation.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import robert.findtransport.presentation.compose.screens.home.HomeScreen
import robert.findtransport.presentation.compose.screens.home.HomeViewModel
import robert.findtransport.presentation.compose.screens.intro.IntroScreen
import robert.findtransport.presentation.compose.screens.stops.StopsPickerScreen
import robert.findtransport.presentation.compose.screens.transport.TransportScreen
import robert.findtransport.presentation.compose.screens.transports.TransportsScreen
import robert.findtransport.utils.EMPTY_TRANSPORT_ID

@Composable
fun Navigation() {
  val navController = rememberNavController()
  val homeViewModel = hiltViewModel<HomeViewModel>()

  val isIntroPassed by homeViewModel.introPassed.collectAsState()
  val startDestination = if (isIntroPassed) {
    NavigationScreens.HomeScreen.name
  } else {
    NavigationScreens.IntroScreen.name
  }

  NavHost(
    navController = navController,
    startDestination = startDestination,
  ) {
    composable(route = NavigationScreens.IntroScreen.name) {
      IntroScreen(navController = navController)
    }
    composable(route = NavigationScreens.HomeScreen.name) {
      HomeScreen(navController = navController, homeViewModel = homeViewModel)
    }
    composable(route = NavigationScreens.TransportsScreen.name) {
      TransportsScreen(navController = navController)
    }
    composable(
      route = "${NavigationScreens.TransportScreen.name}/{transport_id}",
      arguments = listOf(navArgument("transport_id") { type = NavType.IntType }),
    ) { backStackEntry ->
      TransportScreen(
        navController = navController,
        transportId = backStackEntry.arguments?.getInt("transport_id") ?: EMPTY_TRANSPORT_ID,
      )
    }
    composable(
      route = "${NavigationScreens.StopsPickerScreen.name}/{is_from}",
      arguments = listOf(navArgument("is_from") { type = NavType.BoolType }),
    ) { backStackEntry ->
      StopsPickerScreen(
        navController = navController,
        homeViewModel = homeViewModel,
        isFrom = backStackEntry.arguments?.getBoolean("is_from") ?: true,
      )
    }
  }
}

sealed class NavigationScreens(val name: String) {
  object IntroScreen : NavigationScreens("intro_screen")
  object HomeScreen : NavigationScreens("home_screen")
  object TransportsScreen : NavigationScreens("transports_screen")
  object TransportScreen : NavigationScreens("transport_screen")
  object StopsPickerScreen : NavigationScreens("stops_picker_screen")
}
