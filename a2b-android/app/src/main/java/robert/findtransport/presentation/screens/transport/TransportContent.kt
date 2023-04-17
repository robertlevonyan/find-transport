package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.transport.components.StopList
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportContent(
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
  Column(
    modifier = modifier
      .background(MaterialTheme.colorScheme.surface)
      .fillMaxWidth()
  ) {
    StopList(
      transport = transport,
      locale = locale,
      showOptions = showOptions,
      homeViewModel = homeViewModel,
      transportViewModel = transportViewModel,
      navController = navController,
      bottomSheetScaffoldState = bottomSheetScaffoldState,
      scope = scope,
    )
  }
}
