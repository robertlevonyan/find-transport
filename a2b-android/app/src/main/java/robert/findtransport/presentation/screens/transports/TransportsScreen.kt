package robert.findtransport.presentation.screens.transports

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportsScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportsViewModel: TransportsViewModel = hiltViewModel(),
) {
  val locale by transportsViewModel.locale.collectAsState()
  var transportCategory by rememberSaveable { mutableStateOf(TransportCategory.BUS) }

  Scaffold(modifier = modifier, topBar = {
    A2bAppBar(
      title = stringResource(id = R.string.title_transports),
      navigationIcon = R.drawable.ic_arrow_back,
      onNavigationIconClick = { navController.popBackStack() },
    )
  }) { contentPadding ->
    val transports = when (transportCategory) {
      TransportCategory.BUS -> transportsViewModel.buses
      TransportCategory.MICROBUS -> transportsViewModel.microbuses
      TransportCategory.TROLLEYBUS -> transportsViewModel.trolleybuses
      TransportCategory.METRO -> transportsViewModel.metro
    }.collectAsLazyPagingItems()

    TransportsList(
      modifier = Modifier
        .padding(contentPadding),
      transports = transports,
      locale = locale,
      transportCategory = transportCategory,
      onTransportCategoryClick = { transportCategory = it },
      onTransportClick = { transport ->
        navController.navigate(
          route = NavigationScreens.TransportScreen.name + "?transport_id=${transport.id}" + "&show_options=${true}"
        ) {
          navController.graph.route?.let { route ->
            popUpTo(route) { saveState = true }
          }
          launchSingleTop = true
          restoreState = true
        }
      })
  }
}

@Composable
private fun TransportsList(
  modifier: Modifier,
  transports: LazyPagingItems<Transport>,
  locale: String,
  transportCategory: TransportCategory,
  onTransportCategoryClick: (TransportCategory) -> Unit,
  onTransportClick: (Transport) -> Unit,
) {
  LazyColumn(modifier = modifier) {
    item {
      ConstraintLayout(
        modifier = Modifier
          .fillMaxWidth()
          .padding(HalfPadding)
      ) {
        val (busCard, microbusCard, trolleybusCard, metroCard) = createRefs()

        Card(modifier = Modifier
          .padding(HalfPadding)
          .constrainAs(busCard) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(microbusCard.start)
          }) {
          Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
              if (transportCategory != TransportCategory.BUS) {
                onTransportCategoryClick.invoke(TransportCategory.BUS)
              }
            }) {
            TextSecondary(
              modifier = Modifier
                .padding(horizontal = HalfPadding)
                .padding(top = HalfPadding), text = stringResource(id = R.string.label_bus)
            )

            Image(
              modifier = Modifier
                .size(56.dp)
                .align(alignment = Alignment.End),
              painter = painterResource(id = R.drawable.ic_bus_outline),
              contentDescription = null,
            )
          }
        }

        Card(modifier = Modifier
          .padding(HalfPadding)
          .constrainAs(microbusCard) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(busCard.end)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
          }) {
          Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
              if (transportCategory != TransportCategory.MICROBUS) {
                onTransportCategoryClick.invoke(TransportCategory.MICROBUS)
              }
            }) {
            TextSecondary(
              modifier = Modifier
                .padding(horizontal = HalfPadding)
                .padding(top = HalfPadding),
              text = stringResource(id = R.string.label_microbus)
            )

            Image(
              modifier = Modifier
                .size(56.dp)
                .align(alignment = Alignment.End),
              painter = painterResource(id = R.drawable.ic_microbus_outline),
              contentDescription = null,
            )
          }
        }

        Card(modifier = Modifier
          .padding(HalfPadding)
          .constrainAs(trolleybusCard) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(parent.start)
            top.linkTo(busCard.bottom)
            end.linkTo(metroCard.start)
          }) {
          Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
              if (transportCategory != TransportCategory.TROLLEYBUS) {
                onTransportCategoryClick.invoke(TransportCategory.TROLLEYBUS)
              }
            }) {
            TextSecondary(
              modifier = Modifier
                .padding(horizontal = HalfPadding)
                .padding(top = HalfPadding),
              text = stringResource(id = R.string.label_trolleybus)
            )

            Image(
              modifier = Modifier
                .size(56.dp)
                .align(alignment = Alignment.End),
              painter = painterResource(id = R.drawable.ic_trolleybus_outline),
              contentDescription = null,
            )
          }
        }

        Card(modifier = Modifier
          .padding(HalfPadding)
          .constrainAs(metroCard) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(trolleybusCard.end)
            top.linkTo(microbusCard.bottom)
            end.linkTo(parent.end)
          }) {
          Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
              if (transportCategory != TransportCategory.METRO) {
                onTransportCategoryClick.invoke(TransportCategory.METRO)
              }
            }) {
            TextSecondary(
              modifier = Modifier
                .padding(horizontal = HalfPadding)
                .padding(top = HalfPadding),
              text = stringResource(id = R.string.label_metro)
            )

            Image(
              modifier = Modifier
                .size(56.dp)
                .align(alignment = Alignment.End),
              painter = painterResource(id = R.drawable.ic_metro_outline),
              contentDescription = null,
            )
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
