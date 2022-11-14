package robert.findtransport.presentation.screens.transports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.A2bAppBar
import robert.findtransport.presentation.reusables.composables.RowToggleButtonGroup
import robert.findtransport.presentation.reusables.composables.TransportListElement
import robert.findtransport.presentation.reusables.composables.TransportListElementTrailingIcon

@OptIn(ExperimentalMaterial3Api::class)
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
        navController.navigate(
          route = NavigationScreens.TransportScreen.name +
              "?transport_id=${transport.id}" +
              "&show_options=${true}"
        ) {
          navController.graph.route?.let { route ->
            popUpTo(route) { saveState = true }
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
  LazyColumn(modifier = Modifier.padding(contentPadding)) {
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
          shape = Shapes.medium,
          primarySelection = if (showAll) 0 else 1,
          buttonHeight = ToggleButtonSize,
          unselectedColor = MaterialTheme.colorScheme.background,
        ) { index: Int ->
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
          trailingIcon = TransportListElementTrailingIcon.STAR,
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
