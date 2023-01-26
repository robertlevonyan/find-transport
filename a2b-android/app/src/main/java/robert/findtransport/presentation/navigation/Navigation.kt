package robert.findtransport.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import robert.findtransport.data.model.enums.MapType
import robert.findtransport.presentation.screens.feedback.FeedbackScreen
import robert.findtransport.presentation.screens.history.HistoryScreen
import robert.findtransport.presentation.screens.home.HomeScreen
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.intro.IntroScreen
import robert.findtransport.presentation.screens.map.MapScreen
import robert.findtransport.presentation.screens.passing.PassingRoutesScreen
import robert.findtransport.presentation.screens.search.SearchScreen
import robert.findtransport.presentation.screens.settings.SettingsScreen
import robert.findtransport.presentation.screens.stops.StopsPickerScreen
import robert.findtransport.presentation.screens.track.TrackRouteScreen
import robert.findtransport.presentation.screens.transport.TransportScreen
import robert.findtransport.presentation.screens.transports.TransportsScreen
import robert.findtransport.utils.EMPTY_ID

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
      route = "${NavigationScreens.TransportScreen.name}?transport_id={transport_id}&show_options={show_options}",
      arguments = listOf(
        navArgument("transport_id") { type = NavType.IntType },
        navArgument("show_options") { type = NavType.BoolType },
      ),
    ) { backStackEntry ->
      TransportScreen(
        navController = navController,
        transportId = backStackEntry.arguments?.getInt("transport_id") ?: EMPTY_ID,
        showOptions = backStackEntry.arguments?.getBoolean("show_options") ?: true,
        homeViewModel = homeViewModel,
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
    composable(route = NavigationScreens.SettingsScreen.name) {
      SettingsScreen(navController = navController)
    }
    composable(route = NavigationScreens.FeedbackScreen.name) {
      FeedbackScreen(navController = navController)
    }
    composable(route = NavigationScreens.HistoryScreen.name) {
      HistoryScreen(navController = navController)
    }
    composable(
      route = "${NavigationScreens.PassingRoutesScreen.name}/{stop_id}",
      arguments = listOf(navArgument("stop_id") { type = NavType.IntType }),
    ) { backStackEntry ->
      PassingRoutesScreen(
        navController = navController,
        stopId = backStackEntry.arguments?.getInt("stop_id") ?: EMPTY_ID,
      )
    }
    composable(
      route = "${NavigationScreens.SearchScreen.name}?from_id={from_id}&to_id={to_id}&opened={opened}",
      arguments = listOf(
        navArgument("from_id") { type = NavType.IntType },
        navArgument("to_id") { type = NavType.IntType },
        navArgument("opened") { type = NavType.StringType },
      ),
    ) { backStackEntry ->
      SearchScreen(
        navController = navController,
        fromId = backStackEntry.arguments?.getInt("from_id") ?: EMPTY_ID,
        toId = backStackEntry.arguments?.getInt("to_id") ?: EMPTY_ID,
        opened = backStackEntry.arguments?.getString("opened").orEmpty(),
      )
    }
    composable(
      route = "${NavigationScreens.TrackRouteScreen.name}?transport_id={transport_id}&from_id={from_id}&to_id={to_id}",
      arguments = listOf(
        navArgument("transport_id") { type = NavType.IntType },
        navArgument("from_id") { type = NavType.IntType },
        navArgument("to_id") { type = NavType.IntType },
      ),
    ) { backStackEntry ->
      TrackRouteScreen(
        navController = navController,
        transportId = backStackEntry.arguments?.getInt("transport_id") ?: EMPTY_ID,
        fromId = backStackEntry.arguments?.getInt("from_id") ?: EMPTY_ID,
        toId = backStackEntry.arguments?.getInt("to_id") ?: EMPTY_ID,
      )
    }
    composable(
      route = "${NavigationScreens.ChooserMapScreen.name}/{map_type}",
      arguments = listOf(navArgument("map_type") { type = NavType.IntType }),
    ) { backStackEntry ->
      MapScreen(
        navController = navController,
        homeViewModel = homeViewModel,
        mapType = MapType.getByIndex(backStackEntry.arguments?.getInt("map_type") ?: 0),
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
  object SettingsScreen : NavigationScreens("settings_screen")
  object FeedbackScreen : NavigationScreens("feedback_screen")
  object HistoryScreen : NavigationScreens("history_screen")
  object PassingRoutesScreen : NavigationScreens("passing_routes_screen")
  object SearchScreen : NavigationScreens("search_screen")
  object TrackRouteScreen : NavigationScreens("track_route_screen")
  object ChooserMapScreen : NavigationScreens("chooser_map_screen")
}
