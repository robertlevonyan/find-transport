package robert.findtransport.presentation.screens.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.A2bAppBar

@Composable
fun SearchScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  originName: String,
  originLatitude: Float,
  originLongitude: Float,
  originStopId: Int,
  destinationName: String,
  destinationLatitude: Float,
  destinationLongitude: Float,
  destinationStopId: Int,
  opened: String,
  searchViewModel: SearchViewModel = hiltViewModel(),
) {
  LaunchedEffect(key1 = null) {
    searchViewModel.performSearch(
      originStopId = originStopId,
      destinationStopId = destinationStopId,
      originLatitude = originLatitude,
      originLongitude = originLongitude,
      destinationLatitude = destinationLatitude,
      destinationLongitude = destinationLongitude,
      opened = opened,
      originName = originName,
      destinationName = destinationName,
    )
  }

  Scaffold(modifier = modifier, topBar = {
    A2bAppBar(
      title = stringResource(id = R.string.title_search),
      hasFeedbackButton = true,
      navigationIcon = R.drawable.ic_arrow_back,
      onNavigationIconClick = { navController.popBackStack() },
      onFeedbackClick = { navController.navigate(NavigationScreens.FeedbackScreen.name) },
    )
  }) { contentPadding ->
    SearchContent(
      modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      navController = navController,
      searchViewModel = searchViewModel,
      originName = originName,
      destinationName = destinationName,
    )
  }
}
