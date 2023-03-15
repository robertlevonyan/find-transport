package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.transport.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StopList(
  modifier: Modifier,
  transport: Transport,
  locale: String,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  transportViewModel: TransportViewModel,
  navController: NavController,
  bottomSheetScaffoldState: BottomSheetScaffoldState,
  scope: CoroutineScope,
) {
  if (transport == Transport.EMPTY) return
  val isPrimary by transportViewModel.isPrimary.collectAsState()
  val stops = if (isPrimary) transport.stops else transport.stopsReversed
  val isMetro = transport.type == TransportType.METRO

  Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      item {
        Card(modifier = Modifier.padding(bottom = FabPadding)) {
          TransportInfo(
            transport = transport,
            isPrimary = isPrimary,
            locale = locale,
            bottomSheetScaffoldState = bottomSheetScaffoldState,
            onSwapClick = {
              transportViewModel.isPrimary.value = !isPrimary
            },
          ) {
            scope.launch {
              val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
              if (bottomSheetState.isExpanded) {
                bottomSheetState.collapse()
              } else {
                bottomSheetState.expand()
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
}