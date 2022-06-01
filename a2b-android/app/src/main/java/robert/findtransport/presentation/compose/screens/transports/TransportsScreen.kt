package robert.findtransport.presentation.compose.screens.transports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.compose.reusables.*

@Composable
fun TransportsScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportsViewModel: TransportsViewModel = hiltViewModel(),
) {
  transportsViewModel.getTransports(checked = true)
  val locale by transportsViewModel.locale.collectAsState()
  val transports by transportsViewModel.allTransports.collectAsState(initial = emptyList())

  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_transports),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
      )
    }
  ) { contentPadding ->
    LazyColumn(modifier = Modifier.padding(contentPadding)) {
      item {
        TransportTypeChooser(
          onAllButtonClicked = { transportsViewModel.getTransports(checked = true) },
          onFavoritesButtonClicked = { transportsViewModel.getTransports(checked = false) },
        )
      }
      itemsIndexed(
        items = transports,
        itemContent = { index, item ->
          TransportListElement(
            transport = item,
            locale = locale,
            onElementClick = { transport ->

            },
            hasStar = true,
            onStarCheckedChange = { transportsViewModel.toggleTransportFavorite(item) },
          )

          if (index < transports.lastIndex) {
            Divider(color = backgroundColorVariantInvertTransparent())
          }
        },
      )
    }
  }
}

@Composable
fun TransportTypeChooser(
  onAllButtonClicked: () -> Unit,
  onFavoritesButtonClicked: () -> Unit,
) {
  var showAllSelected by rememberSaveable { mutableStateOf(true) }

  Box(modifier = Modifier.fillMaxWidth()) {
    Card(
      modifier = Modifier
        .fillMaxWidth(fraction = 0.9f)
        .align(Alignment.Center)
        .wrapContentHeight(),
      backgroundColor = backgroundColorVariant(),
      shape = Shapes.medium,
    ) {
      Column(modifier = Modifier.padding(HalfPadding)) {
        Text(
          modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(SmallPadding),
          text = stringResource(id = R.string.label_show),
        )
        Row(
          modifier = Modifier
            .align(Alignment.CenterHorizontally),
        ) {
          val squareCorner = CornerSize(0.dp)
          val allButtonColor = if (showAllSelected) Accent else Color.Transparent
          val favoritesButtonColor = if (!showAllSelected) Accent else Color.Transparent

          OutlinedButton(
            shape = Shapes.large.copy(topEnd = squareCorner, bottomEnd = squareCorner),
            border = BorderStroke(1.dp, Accent),
            onClick = {
              if (!showAllSelected) {
                showAllSelected = true
                onAllButtonClicked.invoke()
              }
            },
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = allButtonColor)
          ) {
            Text(
              text = stringResource(id = R.string.label_see_all),
              color = if (showAllSelected) BlackVariant else backgroundColorVariantInvert(),
            )
          }
          OutlinedButton(
            shape = Shapes.large.copy(topStart = squareCorner, bottomStart = squareCorner),
            border = BorderStroke(1.dp, Accent),
            onClick = {
              if (showAllSelected) {
                showAllSelected = false
                onFavoritesButtonClicked.invoke()
              }
            },
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = favoritesButtonColor),
          ) {
            Text(
              text = stringResource(id = R.string.label_see_favorites),
              color = if (!showAllSelected) BlackVariant else backgroundColorVariantInvert(),
            )
          }
        }
      }
    }
  }
}
