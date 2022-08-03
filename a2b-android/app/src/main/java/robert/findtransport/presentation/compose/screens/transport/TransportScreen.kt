package robert.findtransport.presentation.compose.screens.transport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.robertlevonyan.compose.buttontogglegroup.RowToggleButtonGroup
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TransportListElement
import robert.findtransport.utils.EMPTY_TRANSPORT_ID
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun TransportScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportId: Int,
  transportViewModel: TransportViewModel = hiltViewModel(),
) {
  if (transportId == EMPTY_TRANSPORT_ID) {
    navController.popBackStack()
    return
  }
  transportViewModel.getTransport(transportId)

  val locale by transportViewModel.locale.collectAsState()
  val transport by transportViewModel.selectedTransport.collectAsState()

  var showPrimary by rememberSaveable { mutableStateOf(true) }
  val stops = if (showPrimary) transport.stops else transport.stopsReversed

  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_details),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
        additionalActions = {
          IconButton(onClick = { transportViewModel.toggleTransportFavorite(transport) }) {
            val icon = if (transport.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
            Icon(
              painter = painterResource(id = icon),
              contentDescription = stringResource(id = R.string.hint_search),
              tint = MaterialTheme.colors.onSurface,
            )
          }
        }
      )
    }
  ) { contentPadding ->
    StopList(
      modifier = Modifier.padding(contentPadding),
      transport = transport,
      locale = locale,
      onPrimaryRouteClicked = { if (!showPrimary) showPrimary = true },
      onSecondaryRouteClicked = { if (showPrimary) showPrimary = false },
      stops = stops,
    )
  }
}

@Composable
private fun StopList(
  modifier: Modifier,
  transport: Transport,
  locale: String,
  onPrimaryRouteClicked: () -> Unit,
  onSecondaryRouteClicked: () -> Unit,
  stops: List<Stop>,
) {
  if (stops.isEmpty()) return

  val firstStop = stops.first()
  val restOfStops = stops.subList(1, stops.lastIndex - 1)
  val lastStop = stops.last()

  Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      item {
        Card(modifier = Modifier.padding(FabPadding)) {
          TransportListElement(
            transport = transport,
            locale = locale,
            onElementClick = {},
            hasStar = false,
          )
        }
      }
      item {
        Toggles(
          onPrimaryRouteClicked = onPrimaryRouteClicked,
          onSecondaryRouteClicked = onSecondaryRouteClicked,
        )
      }
      item {
        FirstStopCard(
          stop = firstStop,
          locale = locale,
        )
      }
      items(restOfStops) { stop ->
        StopCard(
          stop = stop,
          locale = locale,
        )
      }
      item {
        LastStopCard(
          stop = lastStop,
          locale = locale,
        )
      }
    }

    FloatingActionButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(FabPadding),
      onClick = {

      }) {
      Icon(
        painter = painterResource(id = R.drawable.ic_map),
        contentDescription = stringResource(id = R.string.cd_show_on_map),
      )

    }
  }
}

@Composable
private fun Toggles(
  onPrimaryRouteClicked: () -> Unit,
  onSecondaryRouteClicked: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxWidth()) {
    RowToggleButtonGroup(
      modifier = Modifier
        .align(Alignment.Center)
        .fillMaxWidth(0.9f)
        .padding(vertical = FabPadding),
      buttonCount = 2,
      buttonTexts = arrayOf(
        stringResource(id = R.string.label_primary_route),
        stringResource(id = R.string.label_secondary_route),
      ),
      selectedColor = Accent,
      shape = Shapes.large,
      primarySelection = 0,
      buttonHeight = ToggleButtonSize,
    ) { index ->
      when (index) {
        0 -> onPrimaryRouteClicked.invoke()
        1 -> onSecondaryRouteClicked.invoke()
      }
    }
  }
}

@Composable
private fun FirstStopCard(stop: Stop, locale: String) {
  Box(modifier = Modifier.fillMaxWidth()) {
    Card(
      modifier = Modifier
        .padding(horizontal = FabPadding)
        .fillMaxWidth()
        .wrapContentSize(),
      shape = Shapes.medium,
      backgroundColor = MaterialTheme.colors.surface,
    ) {
      ConstraintLayout(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
      ) {
        val (name, dot, route1, route2, options) = createRefs()
        val dotGuide = createGuidelineFromStart(fraction = 0.08f)
        val centerGuide = createGuidelineFromTop(fraction = 0.5f)

        Box(
          modifier = Modifier
            .width(RouteWidth)
            .constrainAs(route1) {
              height = Dimension.fillToConstraints
              end.linkTo(dotGuide, SmallPadding)
              top.linkTo(centerGuide)
              bottom.linkTo(parent.bottom)
            }
            .background(MaterialTheme.colors.primary),
        )

        Box(
          modifier = Modifier
            .width(RouteWidth)
            .constrainAs(route2) {
              height = Dimension.fillToConstraints
              start.linkTo(dotGuide, SmallPadding)
              top.linkTo(centerGuide)
              bottom.linkTo(parent.bottom)
            }
            .background(MaterialTheme.colors.primary),
        )

        Image(
          modifier = Modifier
            .padding(HalfPadding)
            .size(IconSize)
            .constrainAs(dot) {
              start.linkTo(dotGuide)
              end.linkTo(dotGuide)
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
            },
          painter = rememberAsyncImagePainter(
            ContextCompat.getDrawable(
              LocalContext.current,
              R.drawable.ic_route_dot_start
            )
          ),
          contentDescription = null,
        )

        Text(
          modifier = Modifier
            .padding(horizontal = HalfPadding)
            .padding(top = HalfPadding)
            .padding(bottom = FabPadding)
            .constrainAs(name) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(dot.end)
              end.linkTo(options.start)
              top.linkTo(centerGuide)
              bottom.linkTo(centerGuide)
            },
          text = stop.getCurrentName(locale),
          fontSize = Text13,
        )

        IconButton(
          modifier = Modifier
            .padding(HalfPadding)
            .constrainAs(options) {
              width = Dimension.wrapContent
              height = Dimension.wrapContent
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
              end.linkTo(parent.end)
            },
          onClick = {

          }) {
          Icon(
            painter = painterResource(id = R.drawable.ic_more_vertical),
            tint = MaterialTheme.colors.primary,
            contentDescription = null,
          )
        }
      }
    }
  }
}

@Composable
private fun StopCard(stop: Stop, locale: String) {
  ConstraintLayout(
    modifier = Modifier
      .padding(horizontal = FabPadding)
      .fillMaxWidth()
      .wrapContentHeight()
  ) {
    val (name, dot, route1, route2, options) = createRefs()
    val dotGuide = createGuidelineFromStart(fraction = 0.08f)

    Box(
      modifier = Modifier
        .width(RouteWidth)
        .constrainAs(route1) {
          height = Dimension.fillToConstraints
          end.linkTo(dotGuide, SmallPadding)
          bottom.linkTo(parent.bottom)
          top.linkTo(parent.top)
        }
        .background(MaterialTheme.colors.primary),
    )

    Box(
      modifier = Modifier
        .width(RouteWidth)
        .constrainAs(route2) {
          height = Dimension.fillToConstraints
          start.linkTo(dotGuide, SmallPadding)
          bottom.linkTo(parent.bottom)
          top.linkTo(parent.top)
        }
        .background(MaterialTheme.colors.primary),
    )

    Image(
      modifier = Modifier
        .padding(HalfPadding)
        .size(IconSize)
        .constrainAs(dot) {
          start.linkTo(dotGuide)
          end.linkTo(dotGuide)
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
        },
      painter = rememberAsyncImagePainter(
        ContextCompat.getDrawable(
          LocalContext.current,
          R.drawable.ic_route_dot_end
        )
      ),
      contentDescription = null,
    )

    Text(
      modifier = Modifier
        .padding(horizontal = HalfPadding)
        .padding(top = HalfPadding)
        .padding(bottom = FabPadding)
        .constrainAs(name) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(dot.end)
          end.linkTo(options.start)
          bottom.linkTo(parent.bottom)
          top.linkTo(parent.top)
        },
      text = stop.getCurrentName(locale),
      fontSize = Text13,
    )

    IconButton(
      modifier = Modifier
        .padding(HalfPadding)
        .constrainAs(options) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
          end.linkTo(parent.end)
        },
      onClick = {

      }) {
      Icon(
        painter = painterResource(id = R.drawable.ic_more_white),
        tint = MaterialTheme.colors.primary,
        contentDescription = null,
      )
    }
  }
}

@Composable
private fun LastStopCard(stop: Stop, locale: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = BottomPaddingWithFab)
  ) {
    Card(
      modifier = Modifier
        .padding(horizontal = FabPadding)
        .fillMaxWidth()
        .wrapContentSize(),
      shape = Shapes.medium,
      backgroundColor = MaterialTheme.colors.primary,
    ) {
      ConstraintLayout(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
      ) {
        val (name, dot, route1, route2, options) = createRefs()
        val dotGuide = createGuidelineFromStart(fraction = 0.08f)
        val centerGuide = createGuidelineFromTop(fraction = 0.5f)

        Box(
          modifier = Modifier
            .width(RouteWidth)
            .constrainAs(route1) {
              height = Dimension.fillToConstraints
              end.linkTo(dotGuide, SmallPadding)
              bottom.linkTo(centerGuide)
              top.linkTo(parent.top)
            }
            .background(MaterialTheme.colors.background),
        )

        Box(
          modifier = Modifier
            .width(RouteWidth)
            .constrainAs(route2) {
              height = Dimension.fillToConstraints
              start.linkTo(dotGuide, SmallPadding)
              bottom.linkTo(centerGuide)
              top.linkTo(parent.top)
            }
            .background(MaterialTheme.colors.background),
        )

        Image(
          modifier = Modifier
            .padding(HalfPadding)
            .size(IconSize)
            .constrainAs(dot) {
              start.linkTo(dotGuide)
              end.linkTo(dotGuide)
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
            },
          painter = rememberAsyncImagePainter(
            ContextCompat.getDrawable(
              LocalContext.current,
              R.drawable.ic_route_dot_end
            )
          ),
          contentDescription = null,
        )

        Text(
          modifier = Modifier
            .padding(horizontal = HalfPadding)
            .padding(top = HalfPadding)
            .padding(bottom = FabPadding)
            .constrainAs(name) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(dot.end)
              end.linkTo(options.start)
              bottom.linkTo(centerGuide)
              top.linkTo(centerGuide)
            },
          text = stop.getCurrentName(locale),
          color = MaterialTheme.colors.background,
          fontSize = Text13,
        )

        IconButton(
          modifier = Modifier
            .padding(HalfPadding)
            .constrainAs(options) {
              width = Dimension.wrapContent
              height = Dimension.wrapContent
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
              end.linkTo(parent.end)
            },
          onClick = {

          }) {
          Icon(
            painter = painterResource(id = R.drawable.ic_more_white),
            tint = MaterialTheme.colors.background,
            contentDescription = null,
          )
        }
      }
    }
  }
}
