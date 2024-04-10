package robert.findtransport.presentation.screens.stops.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.A2bAppBar

@Composable
fun TopBar(
  onBackClick: () -> Unit,
  searchBoxStateToggle: () -> Unit,
  onFeedbackClick: () -> Unit,
) {
  A2bAppBar(
    title = stringResource(id = R.string.label_select_stop),
    hasFeedbackButton = true,
    navigationIcon = R.drawable.ic_arrow_back,
    onNavigationIconClick = onBackClick,
    onFeedbackClick = onFeedbackClick,
    additionalActions = {
      IconButton(onClick = searchBoxStateToggle) {
        Icon(
          painter = painterResource(id = R.drawable.ic_search),
          contentDescription = stringResource(id = R.string.hint_search),
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
    }
  )
}
