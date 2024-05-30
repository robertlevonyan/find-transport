package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import robert.findtransport.data.model.StopWithAddress
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.theme.CornerRadius
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.TransportInfoElevation
import robert.findtransport.presentation.reusables.theme.TransportInfoSize
import robert.findtransport.presentation.reusables.composables.BackPressHandler
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.picker.LocationPickerViewModel
import robert.findtransport.utils.EMPTY_ID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    transportId: Int,
    showOptions: Boolean,
    homeViewModel: HomeViewModel,
    transportViewModel: TransportViewModel = hiltViewModel(),
    locationPickerViewModel: LocationPickerViewModel = hiltViewModel(),
) {
    if (transportId == EMPTY_ID) {
        navController.popBackStack()
        return
    }
    transportViewModel.getTransport(transportId)
    val isPrimary by transportViewModel.isPrimary.collectAsState()

    val locale by transportViewModel.locale.collectAsState()
    val transport by transportViewModel.selectedTransport.collectAsState()
    val locationEnabled by locationPickerViewModel.locationEnabled.collectAsState()

    val scope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipPartiallyExpanded = false,
            skipHiddenState = true,
            density = Density(LocalContext.current)
        )
    )
    val lazyColumnState = rememberLazyListState()

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = TransportInfoSize,
        sheetShape = RoundedCornerShape(topStart = CornerRadius, topEnd = CornerRadius),
        sheetContainerColor = MaterialTheme.colorScheme.secondary,
        sheetTonalElevation = TransportInfoElevation,
        sheetShadowElevation = TransportInfoElevation,
        sheetContent = {
            TransportContent(
                modifier = modifier,
                transport = transport,
                locale = locale,
                showOptions = showOptions,
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                lazyColumnState = lazyColumnState,
                isPrimary = isPrimary,
                onPassingRoutesClick = { stop ->
                    navController.navigate(route = "${NavigationScreens.PassingRoutesScreen.name}/${stop.id}")
                },
                onSwapClick = {
                    transportViewModel.isPrimary.value = !transportViewModel.isPrimary.value
                },
                onOriginSelected = { stop ->
                    scope.launch {
                        val address = transportViewModel.getAddress(stop)
                        homeViewModel.setOriginStop(StopWithAddress(stop, address))
                    }
                },
                onDestinationSelected = { stop ->
                    scope.launch {
                        val address = transportViewModel.getAddress(stop)
                        homeViewModel.setDestinationStop(StopWithAddress(stop, address))
                    }
                },
            )
        },
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .padding(HalfPadding)
                    .size(40.dp, 7.dp)
                    .background(
                        color = Color.Black,
                        shape = MaterialTheme.shapes.medium,
                    ),
            )
        }
    ) {
        MapContent(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            locale = locale,
            locationEnabled = locationEnabled,
            transport = transport,
            isPrimary = isPrimary,
            onBackClick = { navController.popBackStack() },
            onFeedbackClick = { navController.navigate(NavigationScreens.FeedbackScreen.name) },
        )
    }

    BackPressHandler {
        val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
        if (bottomSheetState.currentValue == SheetValue.Expanded) {
            scope.launch {
                lazyColumnState.scrollToItem(0)
                bottomSheetState.partialExpand()
            }
        } else {
            navController.popBackStack()
        }
    }
}
