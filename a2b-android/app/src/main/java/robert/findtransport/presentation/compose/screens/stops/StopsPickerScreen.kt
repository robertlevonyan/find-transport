package robert.findtransport.presentation.compose.screens.stops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import robert.findtransport.R
import robert.findtransport.presentation.compose.reusables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.HalfPadding
import robert.findtransport.presentation.compose.reusables.TextMessage
import robert.findtransport.presentation.compose.reusables.backgroundColorVariantInvertTransparent
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun StopsPickerScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  stopsPickerViewModel: StopsPickerViewModel = hiltViewModel(),
) {
  val locale by stopsPickerViewModel.locale.collectAsState()
  val stops = stopsPickerViewModel.allStops.collectAsLazyPagingItems()

  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.label_select_stop),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
        additionalActions = {
          IconButton(onClick = { }) {
            Icon(
              painter = painterResource(id = R.drawable.ic_search_splash),
              contentDescription = stringResource(id = R.string.hint_search),
              tint = Color.Unspecified,
            )
          }
        }
      )
    }
  ) { contentPadding ->
    LazyColumn(modifier = Modifier.padding(contentPadding)) {
      items(stops) { stop ->
        Column {
          TextMessage(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { }
              .padding(HalfPadding),
            text = stop?.getCurrentName(locale) ?: "",
          )

          Divider(color = backgroundColorVariantInvertTransparent(), thickness = 0.5.dp)
        }
      }
    }
  }
}
