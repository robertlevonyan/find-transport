package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.MapType
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.A2bAppBar
import robert.findtransport.presentation.reusables.composables.RowToggleButtonGroup
import robert.findtransport.presentation.reusables.composables.TransportListElement
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.utils.EMPTY_ID
import robert.findtransport.utils.extensions.getCurrentName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportId: Int,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  transportViewModel: TransportViewModel = hiltViewModel(),
) {
  if (transportId == EMPTY_ID) {
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
              tint = MaterialTheme.colorScheme.onSurface,
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
      showOptions = showOptions,
      homeViewModel = homeViewModel,
      navController = navController,
      showPrimary = showPrimary,
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
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  navController: NavController,
  showPrimary: Boolean,
) {
  Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      item {
        Card(modifier = Modifier.padding(FabPadding)) {
          TransportListElement(
            transport = transport,
            locale = locale,
            onElementClick = {},
          )
        }
      }
      item {
        Toggles(
          onPrimaryRouteClicked = onPrimaryRouteClicked,
          onSecondaryRouteClicked = onSecondaryRouteClicked,
        )
      }
      itemsIndexed(stops) { index, stop ->
        when (index) {
          0 -> FirstStopCard(
            stop = stop,
            locale = locale,
            showOptions = showOptions,
            homeViewModel = homeViewModel,
            navController = navController,
          )
          stops.lastIndex -> LastStopCard(
            stop = stop,
            locale = locale,
            showOptions = showOptions,
            homeViewModel = homeViewModel,
            navController = navController,
          )
          else -> StopCard(
            stop = stop,
            locale = locale,
            showOptions = showOptions,
            homeViewModel = homeViewModel,
            navController = navController,
          )
        }
      }
    }

    FloatingActionButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(FabPadding),
      containerColor = MaterialTheme.colorScheme.secondary,
      contentColor = Black,
      onClick = {
        navController.navigate(
          route = NavigationScreens.PreviewMapScreen.name +
              "?map_type=${MapType.PREVIEW.ordinal}" +
              "&transport_id=${transport.id}" +
              "&underground=${transport.type == TransportType.METRO}" +
              "&reversed=${!showPrimary}"
        )
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
      shape = Shapes.medium,
      primarySelection = 0,
      buttonHeight = ToggleButtonSize,
      unselectedColor = MaterialTheme.colorScheme.background,
    ) { index: Int ->
      when (index) {
        0 -> onPrimaryRouteClicked.invoke()
        1 -> onSecondaryRouteClicked.invoke()
      }
    }
  }
}

@Composable
private fun FirstStopCard(
  stop: Stop,
  locale: String,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  navController: NavController,
) {
  Card(
    modifier = Modifier
      .padding(horizontal = FabPadding)
      .fillMaxWidth()
      .wrapContentSize(),
    shape = Shapes.medium,
    colors = CardDefaults.cardColors(containerColor = WhiteVariant),
  ) {
    ConstraintLayout(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
      val (name, dot, route, options) = createRefs()
      val dotGuide = createGuidelineFromStart(fraction = 0.08f)
      val centerGuide = createGuidelineFromTop(fraction = 0.5f)

      Box(
        modifier = Modifier
          .width(RouteWidth)
          .constrainAs(route) {
            height = Dimension.fillToConstraints
            start.linkTo(dot.start)
            end.linkTo(dot.end)
            top.linkTo(centerGuide)
            bottom.linkTo(parent.bottom)
          }
          .background(BlackVariant),
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
          ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_route_dot_start)
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
            end.linkTo(if (showOptions) options.start else parent.end)
            top.linkTo(centerGuide)
            bottom.linkTo(centerGuide)
          },
        text = stop.getCurrentName(locale),
        fontSize = Text13,
        color = BlackVariant,
      )

      if (showOptions) {
        var overflowMenuState by rememberSaveable { mutableStateOf(false) }
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
          onClick = { overflowMenuState = !overflowMenuState }) {
          Icon(
            painter = painterResource(id = R.drawable.ic_more_vertical),
            tint = BlackVariant,
            contentDescription = null,
          )
        }
        PopupMenu(overflowMenuState, homeViewModel, stop, navController) { overflowMenuState = false }
      }
    }
  }
}

@Composable
private fun StopCard(
  stop: Stop,
  locale: String,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  navController: NavController,
) {
  ConstraintLayout(
    modifier = Modifier
      .padding(horizontal = FabPadding)
      .fillMaxWidth()
      .wrapContentHeight()
  ) {
    val (name, dot, route, options) = createRefs()
    val dotGuide = createGuidelineFromStart(fraction = 0.08f)

    Box(
      modifier = Modifier
        .width(RouteWidth)
        .constrainAs(route) {
          height = Dimension.fillToConstraints
          start.linkTo(dot.start)
          end.linkTo(dot.end)
          bottom.linkTo(parent.bottom)
          top.linkTo(parent.top)
        }
        .background(MaterialTheme.colorScheme.onPrimary),
    )

    Image(
      modifier = Modifier
        .padding(HalfPadding)
        .size(SmallIconSize)
        .constrainAs(dot) {
          start.linkTo(dotGuide)
          end.linkTo(dotGuide)
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
        },
      painter = rememberAsyncImagePainter(
        ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_route_dot_normal)
      ),
      colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
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
          end.linkTo(if (showOptions) options.start else parent.end)
          bottom.linkTo(parent.bottom)
          top.linkTo(parent.top)
        },
      text = stop.getCurrentName(locale),
      fontSize = Text11,
    )

    if (showOptions) {
      var overflowMenuState by rememberSaveable { mutableStateOf(false) }
      IconButton(
        modifier = Modifier
          .padding(end = HalfPadding)
          .constrainAs(options) {
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
          },
        onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more_white),
          tint = MaterialTheme.colorScheme.onPrimary,
          contentDescription = null,
        )
        PopupMenu(overflowMenuState, homeViewModel, stop, navController) { overflowMenuState = false }
      }
    }
  }
}

@Composable
private fun LastStopCard(
  stop: Stop,
  locale: String,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  navController: NavController,
) {
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
      colors = CardDefaults.cardColors(containerColor = BlackVariant),
    ) {
      ConstraintLayout(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
      ) {
        val (name, dot, route, options) = createRefs()
        val dotGuide = createGuidelineFromStart(fraction = 0.08f)
        val centerGuide = createGuidelineFromTop(fraction = 0.5f)

        Box(
          modifier = Modifier
            .width(RouteWidth)
            .constrainAs(route) {
              height = Dimension.fillToConstraints
              start.linkTo(dot.start)
              end.linkTo(dot.end)
              bottom.linkTo(centerGuide)
              top.linkTo(parent.top)
            }
            .background(WhiteVariant),
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
              end.linkTo(if (showOptions) options.start else parent.end)
              bottom.linkTo(centerGuide)
              top.linkTo(centerGuide)
            },
          text = stop.getCurrentName(locale),
          color = WhiteVariant,
          fontSize = Text13,
        )

        if (showOptions) {
          var overflowMenuState by rememberSaveable { mutableStateOf(false) }
          IconButton(
            modifier = Modifier
              .padding(end = HalfPadding)
              .constrainAs(options) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
              },
            onClick = { overflowMenuState = !overflowMenuState }) {
            Icon(
              painter = painterResource(id = R.drawable.ic_more_white),
              tint = WhiteVariant,
              contentDescription = null,
            )
            PopupMenu(overflowMenuState, homeViewModel, stop, navController) { overflowMenuState = false }
          }
        }
      }
    }
  }
}

@Composable
private fun PopupMenu(
  showMenu: Boolean,
  homeViewModel: HomeViewModel,
  stop: Stop,
  navController: NavController,
  onMenuDismiss: () -> Unit,
) {
  DropdownMenu(
    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    expanded = showMenu,
    offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
    onDismissRequest = { onMenuDismiss.invoke() },
  ) {
    DropdownMenuItem(
      onClick = {
        onMenuDismiss.invoke()
        homeViewModel.setFromStop(stop)
      },
      text = { Text(text = stringResource(id = R.string.action_set_from)) })
    DropdownMenuItem(
      onClick = {
        onMenuDismiss.invoke()
        homeViewModel.setToStop(stop)
      },
      text = { Text(text = stringResource(id = R.string.action_set_to)) })
    DropdownMenuItem(
      onClick = {
        navController.navigate(route = "${NavigationScreens.PassingRoutesScreen.name}/${stop.id}")
        onMenuDismiss.invoke()
      },
      text = { Text(text = stringResource(id = R.string.action_show)) })
  }
}
