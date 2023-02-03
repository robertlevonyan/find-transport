package robert.findtransport.presentation.screens.search

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.MultiType
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.*
import robert.findtransport.utils.extensions.getCurrentName

@OptIn(ExperimentalMaterial3Api::class)
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
      opened = opened,
      originLatitude = originLatitude,
      originLongitude = originLongitude,
      destinationLatitude = destinationLatitude,
      destinationLongitude = destinationLongitude,
    )
  }

  Scaffold(modifier = modifier, topBar = {
    A2bAppBar(
      title = stringResource(id = R.string.title_search),
      navigationIcon = R.drawable.ic_arrow_back,
      onNavigationIconClick = { navController.popBackStack() },
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

@Composable
private fun SearchContent(
  modifier: Modifier,
  navController: NavController,
  searchViewModel: SearchViewModel,
  originName: String,
  destinationName: String,
) {
  val searchResults by searchViewModel.searchResults.collectAsState()
  val locale by searchViewModel.locale.collectAsState()
  val from by searchViewModel.fromStop.collectAsState()
  val to by searchViewModel.toStop.collectAsState()
  val currentContext = LocalContext.current.applicationContext

  LazyColumn(modifier = modifier) {
    item {
      SearchHeader(
        originName = originName,
        destinationName = destinationName,
      )
    }

    when (searchResults) {
      SearchState.Searching -> item { Loading() }
      is SearchState.Single -> {
        val transports = (searchResults as SearchState.Single).result
        itemsIndexed(transports) { index, transport ->
          TransportListElement(transport = transport, locale = locale) {
            navController.navigate(
              route = NavigationScreens.TrackRouteScreen.name + "?transport_id=${transport.id}"
                  + "&from_id=${from.id}" + "&to_id=${to.id}"
            )
          }

          if (index < transports.lastIndex) {
            Divider(
              color = colorVariantInvertTransparent(),
              thickness = 0.5.dp,
            )
          }
        }
      }
      is SearchState.Multi -> {
        val result = (searchResults as SearchState.Multi).result
        items(result) { multiRouteElement ->
          when (multiRouteElement.type) {
            MultiType.WALK_FROM -> Column(
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
            ) {
              TextSecondary(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.label_walk_from),
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(HalfPadding)
              ) {
                Icon(
                  modifier = Modifier.align(alignment = Alignment.CenterVertically),
                  painter = painterResource(R.drawable.ic_walk),
                  contentDescription = stringResource(id = R.string.label_walk_from),
                )
                TextPrimary(
                  modifier = Modifier.align(alignment = Alignment.CenterVertically),
                  text = multiRouteElement.stop?.getCurrentName(locale).orEmpty(),
                )
              }
            }
            MultiType.WALK_TO -> Column(
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
            ) {
              TextSecondary(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.label_walk_to),
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(HalfPadding)
              ) {
                Icon(
                  modifier = Modifier.align(alignment = Alignment.CenterVertically),
                  painter = painterResource(R.drawable.ic_walk),
                  contentDescription = stringResource(id = R.string.label_walk_to),
                )
                TextPrimary(
                  modifier = Modifier.align(alignment = Alignment.CenterVertically),
                  text = multiRouteElement.stop?.getCurrentName(locale).orEmpty(),
                )
              }
            }
            MultiType.TRANSPORT_TITLE -> Column(
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
            ) {
              TextSecondary(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.label_from2)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(HalfPadding)
              ) {
                Image(
                  modifier = Modifier.align(alignment = Alignment.CenterVertically),
                  painter = painterResource(R.drawable.ic_stop_sign_small),
                  contentDescription = stringResource(id = R.string.label_from2),
                )

                TextPrimary(
                  modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(HalfPadding),
                  text = multiRouteElement.stop?.getCurrentName(locale).orEmpty(),
                )
              }
              TextSecondary(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = FabPadding),
                text = stringResource(id = R.string.label_take_transport),
              )
            }
            MultiType.TRANSPORT -> {
              val transport = multiRouteElement.transport ?: Transport.EMPTY
              TransportListElement(transport = transport,
                locale = locale,
                onElementClick = {
                  navController.navigate(
                    route = NavigationScreens.TransportScreen.name + "?transport_id=${transport.id}"
                        + "&show_options=${false}"
                  )
                })
            }
            MultiType.INTERCHANGE_FROM -> Column(
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
            ) {
              TextSecondary(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.label_interchange_from),
              )
              Row(
                modifier = Modifier
                  .padding(HalfPadding)
                  .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium,
                  )
                  .fillMaxWidth()
              ) {
                Image(
                  modifier = Modifier
                    .size(IconSize)
                    .align(alignment = Alignment.CenterVertically)
                    .padding(start = HalfPadding),
                  painter = painterResource(R.drawable.ic_stop_sign_small),
                  contentDescription = stringResource(id = R.string.label_interchange_from),
                )

                TextPrimary(
                  modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(HalfPadding),
                  text = multiRouteElement.stop?.getCurrentName(locale).orEmpty(),
                  color = MaterialTheme.colorScheme.primary,
                )
              }
            }
            MultiType.INTERCHANGE_TO -> Column(
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
            ) {
              TextSecondary(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.label_interchange_to),
              )
              Row(
                modifier = Modifier
                  .padding(HalfPadding)
                  .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium,
                  )
                  .fillMaxWidth()
              ) {
                Image(
                  modifier = Modifier
                    .size(IconSize)
                    .align(alignment = Alignment.CenterVertically)
                    .padding(start = HalfPadding),
                  painter = painterResource(R.drawable.ic_stop_sign_small),
                  contentDescription = stringResource(id = R.string.label_interchange_to),
                )

                TextPrimary(
                  modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(HalfPadding),
                  text = multiRouteElement.stop?.getCurrentName(locale).orEmpty(),
                  color = MaterialTheme.colorScheme.primary,
                )
              }
            }
          }
        }
      }
      is SearchState.Failed -> {
        Toast.makeText(currentContext, R.string.error_no_routes, Toast.LENGTH_SHORT).show()
        navController.popBackStack()
      }
      SearchState.NotStarted -> return@LazyColumn
    }
  }
}

@Composable
private fun SearchHeader(originName: String, destinationName: String) {
  Card(
    modifier = Modifier
      .padding(horizontal = FabPadding)
      .padding(bottom = FabPadding)
      .fillMaxWidth()
      .wrapContentSize(),
    shape = Shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    ConstraintLayout(modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .clickable {}
      .padding(vertical = HalfPadding)) {
      val (fromIcon, fromStop, toIcon, toStop) = createRefs()
      val guide = createGuidelineFromStart(0.15f)

      Image(
        modifier = Modifier.constrainAs(fromIcon) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(fromStop.top)
          end.linkTo(guide)
          bottom.linkTo(fromStop.bottom)
          start.linkTo(parent.start)
        }, painter = painterResource(id = R.drawable.ic_start_point), contentDescription = null
      )

      TextSecondary(
        modifier = Modifier.constrainAs(fromStop) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(parent.top)
          end.linkTo(parent.end)
          start.linkTo(guide)
        },
        text = originName,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Start,
      )

      Image(
        modifier = Modifier.constrainAs(toIcon) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(toStop.top)
          end.linkTo(guide)
          bottom.linkTo(toStop.bottom)
          start.linkTo(parent.start)
        }, painter = painterResource(id = R.drawable.ic_end_point), contentDescription = null
      )

      TextSecondary(
        modifier = Modifier.constrainAs(toStop) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(fromStop.bottom)
          end.linkTo(parent.end)
          start.linkTo(guide)
          bottom.linkTo(parent.bottom)
        },
        text = destinationName,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Start,
      )
    }
  }
}

@Composable
private fun Loading() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .clickable(enabled = false, onClick = {})
  ) {
    CircularProgressIndicator(
      modifier = Modifier
        .wrapContentSize()
        .align(Alignment.Center), color = MaterialTheme.colorScheme.secondary
    )
  }
}
