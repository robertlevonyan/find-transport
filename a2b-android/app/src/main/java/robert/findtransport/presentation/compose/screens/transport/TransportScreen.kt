package robert.findtransport.presentation.compose.screens.transport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.utils.EMPTY_TRANSPORT_ID

@Composable
fun TransportScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportId: Int,
  transportViewModel: TransportViewModel = hiltViewModel(),
) {
  if (transportId == EMPTY_TRANSPORT_ID) {
    navController.popBackStack()
    return
  }

  val locale by transportViewModel.locale.collectAsState()

}