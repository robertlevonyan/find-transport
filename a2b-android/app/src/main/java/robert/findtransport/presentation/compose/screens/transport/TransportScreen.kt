package robert.findtransport.presentation.compose.screens.transport

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.compose.reusables.A2bAppBar
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
  transportViewModel.getTransport(transportId)

  val locale by transportViewModel.locale.collectAsState()
  val transport by transportViewModel.selectedTransport.collectAsState()

  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_details),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
        additionalActions = {
          IconButton(onClick = { }) {
            val icon = if (transport.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
            Icon(
              painter = painterResource(id = icon),
              contentDescription = stringResource(id = R.string.hint_search),
              tint = Color.Unspecified,
            )
          }
        }
      )
    }
  ) { contentPadding ->
    LazyColumn(modifier = Modifier.padding(contentPadding)) {

    }
  }
}
