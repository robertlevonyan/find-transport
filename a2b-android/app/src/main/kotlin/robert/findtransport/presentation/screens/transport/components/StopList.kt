package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.presentation.reusables.CornerRadius
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.transport.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopList(
  transport: Transport,
  locale: String,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  transportViewModel: TransportViewModel,
  navController: NavController,
  bottomSheetScaffoldState: BottomSheetScaffoldState,
  scope: CoroutineScope,
  isPrimary: Boolean,
) {
  if (transport == Transport.EMPTY) return
  val stops = if (isPrimary) transport.stops else transport.stopsReversed
  val isMetro = transport.type == TransportType.METRO

  LazyColumn(
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Card(
        modifier = Modifier.padding(bottom = FabPadding),
        shape = RoundedCornerShape(bottomEnd = CornerRadius, bottomStart = CornerRadius),
      ) {
        TransportInfo(
          transport = transport,
          isPrimary = isPrimary,
          locale = locale,
          onSwapClick = {
            transportViewModel.isPrimary.value = !transportViewModel.isPrimary.value
          },
        ) {
          scope.launch {
            val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
            when (bottomSheetState.currentValue) {
              SheetValue.Expanded -> bottomSheetState.partialExpand()
              SheetValue.PartiallyExpanded -> bottomSheetState.expand()
              SheetValue.Hidden -> return@launch
            }
          }
        }
      }
    }
    itemsIndexed(stops) { index, stop ->
      when (index) {
        0 -> FirstStopCard(
          stop = stop,
          locale = locale,
          showOptions = showOptions,
          homeViewModel = homeViewModel,
          transportViewModel = transportViewModel,
          navController = navController,
        )

        stops.lastIndex -> LastStopCard(
          stop = stop,
          locale = locale,
          showOptions = showOptions,
          homeViewModel = homeViewModel,
          transportViewModel = transportViewModel,
          navController = navController,
        )

        else -> StopCard(
          stop = stop,
          locale = locale,
          showOptions = showOptions,
          homeViewModel = homeViewModel,
          transportViewModel = transportViewModel,
          navController = navController,
        )
      }
    }
  }
}