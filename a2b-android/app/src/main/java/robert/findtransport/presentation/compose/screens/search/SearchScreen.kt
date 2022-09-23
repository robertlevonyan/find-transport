package robert.findtransport.presentation.compose.screens.search

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
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
import robert.findtransport.data.model.MultiRouteCase
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.FabPadding
import robert.findtransport.presentation.compose.reusables.HalfPadding
import robert.findtransport.presentation.compose.reusables.Shapes
import robert.findtransport.presentation.compose.reusables.colorVariantInvertTransparent
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TextMessage
import robert.findtransport.presentation.compose.reusables.composables.TransportListElement
import robert.findtransport.presentation.compose.reusables.composables.TransportListElementTrailingIcon
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun SearchScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  fromId: Int,
  toId: Int,
  opened: String,
  searchViewModel: SearchViewModel = hiltViewModel(),
) {
  LaunchedEffect(key1 = null) {
    searchViewModel.performSearch(fromId = fromId, toId = toId, opened = opened)
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_search),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
      )
    }
  ) { contentPadding ->
    SearchContent(
      modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      navController = navController,
      searchViewModel = searchViewModel,
    )
  }
}

@Composable
private fun SearchContent(
  modifier: Modifier,
  navController: NavController,
  searchViewModel: SearchViewModel,
) {
  val searchResults by searchViewModel.searchResults.collectAsState()
  val locale by searchViewModel.locale.collectAsState()
  val from by searchViewModel.fromStop.collectAsState()
  val to by searchViewModel.toStop.collectAsState()
  val currentContext = LocalContext.current.applicationContext

  LazyColumn(modifier = modifier) {
    item {
      Card(
        modifier = Modifier
          .padding(horizontal = FabPadding)
          .padding(bottom = FabPadding)
          .fillMaxWidth()
          .wrapContentSize(),
        shape = Shapes.large,
        backgroundColor = MaterialTheme.colors.surface,
      ) {
        ConstraintLayout(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {}
            .padding(horizontal = FabPadding, vertical = HalfPadding)
        ) {
          val (fromIcon, fromStop, toIcon, toStop) = createRefs()
          val guide = createGuidelineFromStart(0.25f)

          Image(
            modifier = Modifier
              .constrainAs(fromIcon) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(fromStop.top)
                end.linkTo(guide)
                bottom.linkTo(fromStop.bottom)
                start.linkTo(parent.start)
              },
            painter = painterResource(id = R.drawable.ic_start_point),
            contentDescription = null
          )

          TextMessage(
            modifier = Modifier
              .constrainAs(fromStop) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                start.linkTo(guide)
              },
            text = from.getCurrentName(locale),
            color = MaterialTheme.colors.onPrimary,
            textAlign = TextAlign.Start,
          )

          Image(
            modifier = Modifier
              .constrainAs(toIcon) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(toStop.top)
                end.linkTo(guide)
                bottom.linkTo(toStop.bottom)
                start.linkTo(parent.start)
              },
            painter = painterResource(id = R.drawable.ic_end_point),
            contentDescription = null
          )

          TextMessage(
            modifier = Modifier
              .constrainAs(toStop) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(fromStop.bottom)
                end.linkTo(parent.end)
                start.linkTo(guide)
                bottom.linkTo(parent.bottom)
              },
            text = to.getCurrentName(locale),
            color = MaterialTheme.colors.onPrimary,
            textAlign = TextAlign.Start,
          )
        }
      }
    }

    when (searchResults) {
      SearchState.Searching -> item {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = false, onClick = {})
        ) {
          CircularProgressIndicator(
            modifier = Modifier
              .wrapContentSize()
              .align(Alignment.Center),
            color = MaterialTheme.colors.secondary
          )
        }
      }
      is SearchState.Single -> {
        val transports = (searchResults as SearchState.Single).result
        itemsIndexed(transports) { index, transport ->
          TransportListElement(
            transport = transport,
            locale = locale,
            trailingIcon = TransportListElementTrailingIcon.TRACK,
            onTrackClick = {},
            onElementClick = {
              navController.navigate(
                route = NavigationScreens.TransportScreen.name +
                    "?transport_id=${transport.id}" +
                    "&show_options=${false}"
              )
            })

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
          when (multiRouteElement.case) {
            MultiRouteCase.SINGLE_FROM -> {}
            MultiRouteCase.SINGLE_TO -> {}
            MultiRouteCase.FROM_TO -> {}
          }
//          when (multiRouteElement.type) {
//            MultiType.WALK_FROM -> TODO()
//            MultiType.WALK_TO -> TODO()
//            MultiType.TRANSPORT_TITLE -> TODO()
//            MultiType.TRANSPORT -> TODO()
//            MultiType.INTERCHANGE_FROM -> TODO()
//            MultiType.INTERCHANGE_TO -> TODO()
//          }
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
