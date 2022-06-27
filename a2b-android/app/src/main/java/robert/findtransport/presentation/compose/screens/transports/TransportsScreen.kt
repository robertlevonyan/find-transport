package robert.findtransport.presentation.compose.screens.transports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.robertlevonyan.compose.buttontogglegroup.RowToggleButtonGroup
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TransportListElement

@Composable
fun TransportsScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportsViewModel: TransportsViewModel = hiltViewModel(),
) {
  val locale by transportsViewModel.locale.collectAsState()
  val allTransports = transportsViewModel.allTransports.collectAsLazyPagingItems()
  val favoriteTransports = transportsViewModel.favoriteTransports.collectAsLazyPagingItems()
  var showAll by rememberSaveable { mutableStateOf(true) }

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
    val transports = if (showAll) allTransports else favoriteTransports

    TransportsList(
      contentPadding = contentPadding,
      transports = transports,
      locale = locale,
      showAll = showAll,
      onToggleClick = { showAll = it },
      onStarCheckedChange = transportsViewModel::toggleTransportFavorite,
      onTransportClick = { transport ->
        navController.navigate(route = "${NavigationScreens.TransportScreen.name}/${transport.id}") {
          navController.graph.route?.let { route ->
            popUpTo(route) {
              saveState = true
            }
          }
          launchSingleTop = true
          restoreState = true
        }
      }
    )
  }
}

@Composable
private fun TransportsList(
  contentPadding: PaddingValues,
  transports: LazyPagingItems<Transport>,
  locale: String,
  showAll: Boolean,
  onToggleClick: (Boolean) -> Unit,
  onStarCheckedChange: (Transport) -> Unit,
  onTransportClick: (Transport) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.padding(contentPadding),
    contentPadding = contentPadding,
  ) {
    item {
      Box(modifier = Modifier.fillMaxWidth()) {
        RowToggleButtonGroup(
          modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = FabPadding)
            .align(Alignment.Center),
          buttonCount = 2,
          buttonTexts = arrayOf(
            stringResource(id = R.string.label_see_all),
            stringResource(id = R.string.label_see_favorites),
          ),
          selectedColor = Accent,
          shape = Shapes.large,
          primarySelection = if (showAll) 0 else 1,
          buttonHeight = ToggleButtonSize,
        ) { index ->
          when (index) {
            0 -> onToggleClick.invoke(true)
            1 -> onToggleClick.invoke(false)
          }
        }
      }
    }
    itemsIndexed(
      items = transports,
      itemContent = { index, item ->
        item ?: return@itemsIndexed

        TransportListElement(
          transport = item,
          locale = locale,
          onElementClick = onTransportClick,
          hasStar = true,
          onStarCheckedChange = { onStarCheckedChange.invoke(item) },
        )

        if (index < transports.itemCount - 1) {
          Divider(
            color = colorVariantInvertTransparent(),
            thickness = 0.5.dp,
          )
        }
      },
    )
  }
}

@Composable
private fun TransportTypeChooser(
  showAll: Boolean,
  onAllButtonClicked: () -> Unit,
  onFavoritesButtonClicked: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxWidth()) {
    Card(
      modifier = Modifier
        .fillMaxWidth(fraction = 0.9f)
        .align(Alignment.Center)
        .wrapContentHeight(),
      backgroundColor = colorVariant(),
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
          val allButtonColor = if (showAll) Accent else Color.Transparent
          val favoritesButtonColor = if (!showAll) Accent else Color.Transparent

          OutlinedButton(
            shape = Shapes.large.copy(topEnd = squareCorner, bottomEnd = squareCorner),
            border = BorderStroke(1.dp, Accent),
            onClick = {
              if (!showAll) {
                onAllButtonClicked.invoke()
              }
            },
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = allButtonColor)
          ) {
            Text(
              text = stringResource(id = R.string.label_see_all),
              color = if (showAll) BlackVariant else colorVariantInvert(),
            )
          }
          OutlinedButton(
            shape = Shapes.large.copy(topStart = squareCorner, bottomStart = squareCorner),
            border = BorderStroke(1.dp, Accent),
            onClick = {
              if (showAll) {
                onFavoritesButtonClicked.invoke()
              }
            },
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = favoritesButtonColor),
          ) {
            Text(
              text = stringResource(id = R.string.label_see_favorites),
              color = if (!showAll) BlackVariant else colorVariantInvert(),
            )
          }
        }
      }
    }
  }
}
