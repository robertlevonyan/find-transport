package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.BackPressHandler
import robert.findtransport.presentation.reusables.composables.getMapStyle
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

  val mapStyle = getMapStyle()
  val scope = rememberCoroutineScope()
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
    bottomSheetState = SheetState(
      initialValue = SheetValue.PartiallyExpanded,
      skipPartiallyExpanded = false,
      skipHiddenState = true,
    )
  )

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
        homeViewModel = homeViewModel,
        transportViewModel = transportViewModel,
        navController = navController,
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        scope = scope,
        isPrimary = isPrimary,
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
      modifier = Modifier.fillMaxSize(),
      navController = navController,
      locale = locale,
      locationEnabled = locationEnabled,
      mapStyle = mapStyle,
      transport = transport,
      isPrimary = isPrimary,
    )
  }

  BackPressHandler {
    val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
    if (bottomSheetState.currentValue == SheetValue.Expanded) {
      scope.launch { bottomSheetState.partialExpand() }
    } else {
      navController.popBackStack()
    }
  }
}
