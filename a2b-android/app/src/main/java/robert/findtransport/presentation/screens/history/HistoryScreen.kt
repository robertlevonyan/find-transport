package robert.findtransport.presentation.screens.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.reusables.composables.A2bAppBar

@Composable
fun HistoryScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  historyViewModel: HistoryViewModel = hiltViewModel(),
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_history),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
      )
    }
  ) { contentPadding ->
    HistoryContent(
      modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      navController = navController,
      historyViewModel = historyViewModel,
    )
  }
}
