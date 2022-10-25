package robert.findtransport.presentation.compose.screens.stops

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import robert.findtransport.R
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TextSecondary
import robert.findtransport.presentation.compose.screens.home.HomeViewModel
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun StopsPickerScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  stopsPickerViewModel: StopsPickerViewModel = hiltViewModel(),
  homeViewModel: HomeViewModel,
  isFrom: Boolean,
) {
  val locale by stopsPickerViewModel.locale.collectAsState()
  val stops = stopsPickerViewModel.allStops.collectAsLazyPagingItems()
  var searchBoxState by rememberSaveable { mutableStateOf(false) }

  stopsPickerViewModel.findStops("")

  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.label_select_stop),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
        additionalActions = {
          IconButton(onClick = {
            searchBoxState = !searchBoxState
            if (!searchBoxState) {
              stopsPickerViewModel.findStops("")
            }
          }) {
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
    Column(modifier = Modifier.padding(contentPadding)) {
      AnimatedVisibility(visible = searchBoxState) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FabPadding)
        ) { SearchInput(stopsPickerViewModel::findStops) }
      }
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(stops) { stop ->
          stop ?: return@items

          Column {
            TextSecondary(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  if (isFrom) {
                    homeViewModel.setFromStop(stop)
                  } else {
                    homeViewModel.setToStop(stop)
                  }
                  navController.popBackStack()
                }
                .padding(HalfPadding),
              text = stop.getCurrentName(locale),
              textAlign = TextAlign.Start,
            )

            Divider(
              color = colorVariantInvertTransparent(),
              thickness = 0.5.dp,
              startIndent = FabPadding,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SearchInput(onValueChange: (String) -> Unit) {
  var inputText by rememberSaveable { mutableStateOf("") }

  OutlinedTextField(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = HalfPadding)
      .padding(bottom = HalfPadding),
    value = inputText,
    onValueChange = {
      inputText = it
      onValueChange.invoke(it)
    },
    singleLine = true,
    shape = Shapes.medium,
    label = { Text(text = stringResource(id = R.string.hint_search)) },
    colors = TextFieldDefaults.outlinedTextFieldColors(
      backgroundColor = searchInputBackgroundColor(),
      focusedBorderColor = MaterialTheme.colors.surface,
      unfocusedBorderColor = MaterialTheme.colors.surface,
      disabledBorderColor = MaterialTheme.colors.surface,
      errorBorderColor = MaterialTheme.colors.error,
      cursorColor = MaterialTheme.colors.onSurface,
      focusedLabelColor = MaterialTheme.colors.onSurface,
    ),
    textStyle = TextStyle(
      color = MaterialTheme.colors.onSurface,
      fontFamily = FontFamily(Font(R.font.google_sans_regular)),
    ),
  )
}
