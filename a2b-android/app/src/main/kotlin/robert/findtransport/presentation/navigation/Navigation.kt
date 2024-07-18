package robert.findtransport.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import robert.findtransport.data.model.enums.StopType
import robert.findtransport.presentation.screens.feedback.FeedbackScreen
import robert.findtransport.presentation.screens.history.HistoryScreen
import robert.findtransport.presentation.screens.home.HomeScreen
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.intro.IntroScreen
import robert.findtransport.presentation.screens.passingroutes.PassingRoutesScreen
import robert.findtransport.presentation.screens.picker.LocationPickerScreen
import robert.findtransport.presentation.screens.search.SearchScreen
import robert.findtransport.presentation.screens.settings.SettingsScreen
import robert.findtransport.presentation.screens.stops.StopsPickerScreen
import robert.findtransport.presentation.screens.track.TrackRouteScreen
import robert.findtransport.presentation.screens.transport.TransportScreen
import robert.findtransport.presentation.screens.transports.TransportsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val homeViewModel = hiltViewModel<HomeViewModel>()

    val isIntroPassed by homeViewModel.introPassed.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isIntroPassed) NavigationScreens.HomeScreen else NavigationScreens.IntroScreen,
    ) {
        composable<NavigationScreens.IntroScreen> {
            IntroScreen(navController = navController)
        }
        composable<NavigationScreens.HomeScreen> {
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }
        composable<NavigationScreens.TransportsScreen> {
            TransportsScreen(navController = navController)
        }
        composable<NavigationScreens.TransportScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationScreens.TransportScreen>()
            TransportScreen(
                navController = navController,
                transportId = args.transportId,
                showOptions = args.showOptions,
                homeViewModel = homeViewModel,
            )
        }
        composable<NavigationScreens.StopsPickerScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationScreens.StopsPickerScreen>()
            StopsPickerScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                isFrom = args.isFrom,
            )
        }
        composable<NavigationScreens.SettingsScreen> {
            SettingsScreen(navController = navController)
        }
        composable<NavigationScreens.FeedbackScreen> {
            FeedbackScreen(navController = navController)
        }
        composable<NavigationScreens.HistoryScreen> {
            HistoryScreen(navController = navController)
        }
        composable<NavigationScreens.PassingRoutesScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationScreens.PassingRoutesScreen>()
            PassingRoutesScreen(
                navController = navController,
                stopId = args.stopId,
            )
        }
        composable<NavigationScreens.SearchScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationScreens.SearchScreen>()
            SearchScreen(
                navController = navController,
                originName = args.originName,
                originLatitude = args.originLatitude,
                originLongitude = args.originLongitude,
                originStopId = args.originStopId,
                destinationName = args.destinationName,
                destinationLatitude = args.destinationLatitude,
                destinationLongitude = args.destinationLongitude,
                destinationStopId = args.destinationStopId,
                opened = args.opened,
            )
        }
        composable<NavigationScreens.TrackRouteScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationScreens.TrackRouteScreen>()
            TrackRouteScreen(
                navController = navController,
                transportId = args.transportId,
                fromId = args.fromId,
                toId = args.toId,
            )
        }
        composable<NavigationScreens.LocationPicker> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationScreens.LocationPicker>()
            LocationPickerScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                pickerType = StopType.entries[args.type]
            )
        }
    }
}

sealed class NavigationScreens {
    @Serializable
    data object IntroScreen : NavigationScreens()

    @Serializable
    data object HomeScreen : NavigationScreens()

    @Serializable
    data object TransportsScreen : NavigationScreens()

    @Serializable
    data class TransportScreen(
        val transportId: Int,
        val showOptions: Boolean,
    ) : NavigationScreens()

    @Serializable
    data class StopsPickerScreen(val isFrom: Boolean) : NavigationScreens()

    @Serializable
    data object SettingsScreen : NavigationScreens()

    @Serializable
    data object FeedbackScreen : NavigationScreens()

    @Serializable
    data object HistoryScreen : NavigationScreens()

    @Serializable
    data class PassingRoutesScreen(val stopId: Int) : NavigationScreens()

    @Serializable
    data class SearchScreen(
        val originName: String,
        val originLatitude: Float,
        val originLongitude: Float,
        val originStopId: Int,
        val destinationName: String,
        val destinationLatitude: Float,
        val destinationLongitude: Float,
        val destinationStopId: Int,
        val opened: String,
    ) : NavigationScreens()

    @Serializable
    data class TrackRouteScreen(
        val transportId: Int,
        val fromId: Int,
        val toId: Int,
    ) : NavigationScreens()

    @Serializable
    data class LocationPicker(val type: Int) : NavigationScreens()
}
