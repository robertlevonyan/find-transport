package robert.findtransport.presentation.screens.transport

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.model.RouteResult
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toApiStop
import robert.findtransport.domain.mapper.toJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.BackPressHandler
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.map.LocationPickerViewModel
import robert.findtransport.presentation.screens.map.enableLocationComponent
import robert.findtransport.presentation.screens.map.getMapStyle
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.EMPTY_ID
import robert.findtransport.utils.STOP_ICON_SIZE
import robert.findtransport.utils.extensions.*
import kotlin.math.abs

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransportScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  transportId: Int,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  transportViewModel: TransportViewModel = hiltViewModel(),
  locationPickerViewModel: LocationPickerViewModel = hiltViewModel(),
  previewMapViewModel: PreviewLocationPickerViewModel = hiltViewModel(),
) {
  if (transportId == EMPTY_ID) {
    navController.popBackStack()
    return
  }
  transportViewModel.getTransport(transportId)

  val locale by transportViewModel.locale.collectAsState()
  val transport by transportViewModel.selectedTransport.collectAsState()
  val locationEnabled by locationPickerViewModel.locationEnabled.collectAsState()

  val mapStyle = getMapStyle()
  val scope = rememberCoroutineScope()
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

  BottomSheetScaffold(
    scaffoldState = bottomSheetScaffoldState,
    sheetShape = RoundedCornerShape(topStart = CornerRadius, topEnd = CornerRadius),
    sheetElevation = TransportInfoElevation,
    sheetBackgroundColor = Color.Transparent,
    sheetPeekHeight = TransportInfoSize,
    sheetContent = {
      Column(
        modifier = modifier
          .background(MaterialTheme.colorScheme.surface)
          .fillMaxWidth()
      ) {
        StopList(
          modifier = Modifier,
          transport = transport,
          locale = locale,
          showOptions = showOptions,
          homeViewModel = homeViewModel,
          navController = navController,
          bottomSheetScaffoldState = bottomSheetScaffoldState,
          scope = scope,
          previewMapViewModel = previewMapViewModel,
        )
      }
    },
  ) {
    MapContent(
      modifier = Modifier.fillMaxSize(),
      navController = navController,
      locale = locale,
      locationEnabled = locationEnabled,
      mapStyle = mapStyle,
      previewMapViewModel = previewMapViewModel,
      scope = scope,
    )
  }

  BackPressHandler {
    val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
    if (bottomSheetState.isExpanded) {
      scope.launch { bottomSheetState.collapse() }
    } else {
      navController.popBackStack()
    }
  }
}

@Composable
private fun MapContent(
  modifier: Modifier,
  navController: NavController,
  locale: String,
  locationEnabled: Boolean,
  mapStyle: String,
  previewMapViewModel: PreviewLocationPickerViewModel,
  scope: CoroutineScope,
) {
  Box(modifier = modifier) {
    MapView(
      navController = navController,
      locale = locale,
      locationEnabled = locationEnabled,
      mapStyle = mapStyle,
      previewMapViewModel = previewMapViewModel,
      scope = scope,
    )

    SmallFloatingActionButton(modifier = Modifier.padding(
      vertical = FabPadding, horizontal = HalfPadding
    ),
      containerColor = MaterialTheme.colorScheme.secondary,
      onClick = { navController.popBackStack() }) {
      Icon(
        painter = painterResource(id = R.drawable.ic_arrow_back),
        contentDescription = stringResource(id = R.string.cd_current_location),
      )
    }
  }
}

@Composable
private fun MapView(
  navController: NavController,
  locale: String,
  locationEnabled: Boolean,
  mapStyle: String,
  previewMapViewModel: PreviewLocationPickerViewModel,
  scope: CoroutineScope,
) {
  AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
    ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_TOKEN)
    MapView(context = context)
  }, update = { mapView ->
    val map = mapView.getMapboxMap()
    val context = mapView.context

    val pointAnnotationManager = mapView.annotations.createPointAnnotationManager().apply {
      addClickListener(OnPointAnnotationClickListener { pointAnnotation ->
        pointAnnotation.getData()?.let { data ->
          val stop = data.fromJson<robert.findtransport.data.entity.Stop>().toStop()
          context.showToast(stop.getCurrentName(locale))
        }
        true
      })
    }
    val polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager().apply {
      lineCap = LineCap.ROUND
    }

    map.loadStyleUri(styleUri = mapStyle, onStyleLoaded = {
      if (locationEnabled) {
        mapView.enableLocationComponent()
      }

      map.setCamera(CameraOptions.Builder().zoom(11.0).build())

      scope.launch {
        previewMapViewModel.route.collectLatest { result ->
          handleRoute(
            result = result,
            reversed = previewMapViewModel.isPrimary.value,
            context = context,
            polylineAnnotationManager = polylineAnnotationManager,
            map = map,
            pointAnnotationManager = pointAnnotationManager,
            navController = navController,
          )
        }
      }
    })
  })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransportInfo(
  transport: Transport,
  locale: String,
  bottomSheetScaffoldState: BottomSheetScaffoldState,
  previewMapViewModel: PreviewLocationPickerViewModel,
  onSwapClick: () -> Unit,
  onElementClick: () -> Unit,
) {
  val isPrimary by previewMapViewModel.isPrimary.collectAsState()

  Column(modifier = Modifier
    .fillMaxSize()
    .background(color = MaterialTheme.colorScheme.secondary)
    .clickable { onElementClick.invoke() }) {
    Box(
      modifier = Modifier
        .padding(HalfPadding)
        .size(80.dp, 5.dp)
        .align(Alignment.CenterHorizontally)
        .background(
          color = Color.Black,
          shape = MaterialTheme.shapes.medium,
        ),
    )
    ConstraintLayout(
      modifier = Modifier
        .fillMaxWidth(fraction = 0.9f)
        .wrapContentHeight()
        .align(Alignment.CenterHorizontally)
    ) {
      val icon = transport.getIcon()

      val (transportIcon, transportNumber, startIcon,
        firstStop, endIcon, lastStop, stopCount, swap) = createRefs()

      AsyncImage(
        modifier = Modifier
          .size(BarIconSize)
          .constrainAs(transportIcon) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
          },
        model = ImageRequest.Builder(context = LocalContext.current).data(icon).build(),
        contentDescription = null,
      )

      Text(
        modifier = Modifier
          .wrapContentWidth()
          .padding(start = HalfPadding)
          .constrainAs(transportNumber) {
            start.linkTo(transportIcon.end)
            top.linkTo(parent.top)
            bottom.linkTo(transportIcon.bottom)
          },
        text = transport.getNameFormatted(),
        color = Color.Black,
        fontWeight = FontWeight.Black,
        fontSize = TextTransportNumber,
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )

      val stops = transport.stops
      val first = stops.first()
      val last = stops.last()

      val stopsText = buildAnnotatedString {
        withStyle(SpanStyle(fontSize = Text24)) {
          append(stops.size.toString())
        }
        append("\n")
        withStyle(SpanStyle(fontSize = Text12)) {
          append(stringResource(id = R.string.label_stops))
        }
      }

      Text(
        modifier = Modifier
          .wrapContentSize()
          .constrainAs(stopCount) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
          },
        text = stopsText,
        textAlign = TextAlign.Center,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )

      val alphaValue = bottomSheetScaffoldState.bottomSheetState.offset.value / 1000
      var alpha = when {
        alphaValue < 0 -> 0f
        alphaValue > 1 -> 1f
        else -> alphaValue
      }
      alpha = abs(1 - alpha)

      IconButton(
        modifier = Modifier
          .alpha(alpha)
          .constrainAs(swap) {
            end.linkTo(parent.end)
            top.linkTo(stopCount.bottom)
            bottom.linkTo(parent.bottom)
          },
        onClick = { onSwapClick.invoke() },
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_swap),
          contentDescription = null,
          colorFilter = ColorFilter.tint(Color.Black),
        )
      }

      Image(
        painter = painterResource(id = R.drawable.ic_start_point),
        contentDescription = null,
        modifier = Modifier
          .size(IconSize)
          .alpha(alpha)
          .constrainAs(startIcon) {
            start.linkTo(parent.start)
            end.linkTo(firstStop.start)
            top.linkTo(firstStop.top)
            bottom.linkTo(firstStop.bottom)
          },
      )
      TextPrimary(
        modifier = Modifier
          .alpha(alpha)
          .constrainAs(firstStop) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(startIcon.end)
            end.linkTo(swap.start)
            top.linkTo(transportIcon.bottom)
            bottom.linkTo(lastStop.top)
          },
        text = (if (isPrimary) first else last).getCurrentName(locale),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = Color.Black,
      )

      Image(
        painter = painterResource(id = R.drawable.ic_end_point),
        contentDescription = null,
        modifier = Modifier
          .size(IconSize)
          .alpha(alpha)
          .constrainAs(endIcon) {
            start.linkTo(parent.start)
            end.linkTo(lastStop.start)
            top.linkTo(lastStop.top)
            bottom.linkTo(lastStop.bottom)
          },
      )
      TextSecondary(
        modifier = Modifier
          .alpha(alpha)
          .constrainAs(lastStop) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(endIcon.end)
            end.linkTo(swap.start)
            top.linkTo(firstStop.bottom)
            bottom.linkTo(parent.bottom)
          },
        text = (if (isPrimary) last else first).getCurrentName(locale),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = Color.Black,
      )
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StopList(
  modifier: Modifier,
  transport: Transport,
  locale: String,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  navController: NavController,
  bottomSheetScaffoldState: BottomSheetScaffoldState,
  scope: CoroutineScope,
  previewMapViewModel: PreviewLocationPickerViewModel,
) {
  if (transport == Transport.EMPTY) return
  var isPrimary by rememberSaveable { mutableStateOf(true) }
  val stops = if (isPrimary) transport.stops else transport.stopsReversed
  val isMetro = transport.type == TransportType.METRO
  if (isPrimary) {
    previewMapViewModel.getReversedTransportRoute(transport.id, isMetro)
  } else {
    previewMapViewModel.getTransportRoute(transport.id, isMetro)
  }

  Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      item {
        Card(modifier = Modifier.padding(bottom = FabPadding)) {
          TransportInfo(
            transport = transport,
            locale = locale,
            bottomSheetScaffoldState = bottomSheetScaffoldState,
            previewMapViewModel = previewMapViewModel,
            onSwapClick = {
              isPrimary = !isPrimary
              previewMapViewModel.isPrimary.value = isPrimary
            },
          ) {
            scope.launch {
              val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
              if (bottomSheetState.isExpanded) {
                bottomSheetState.collapse()
              } else {
                bottomSheetState.expand()
              }
            }
          }
        }
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
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        color = BlackVariant,
      )

      if (showOptions) {
        var overflowMenuState by rememberSaveable { mutableStateOf(false) }
        IconButton(modifier = Modifier
          .padding(HalfPadding)
          .constrainAs(options) {
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
          }, onClick = { overflowMenuState = !overflowMenuState }) {
          Icon(
            painter = painterResource(id = R.drawable.ic_more),
            tint = BlackVariant,
            contentDescription = null,
          )
        }
        PopupMenu(overflowMenuState, homeViewModel, stop, navController) {
          overflowMenuState = false
        }
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
      fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
    )

    if (showOptions) {
      var overflowMenuState by rememberSaveable { mutableStateOf(false) }
      IconButton(modifier = Modifier
        .padding(end = HalfPadding)
        .constrainAs(options) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
          end.linkTo(parent.end)
        }, onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more),
          tint = MaterialTheme.colorScheme.onPrimary,
          contentDescription = null,
        )
        PopupMenu(overflowMenuState, homeViewModel, stop, navController) {
          overflowMenuState = false
        }
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
              LocalContext.current, R.drawable.ic_route_dot_end
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
          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        )

        if (showOptions) {
          var overflowMenuState by rememberSaveable { mutableStateOf(false) }
          IconButton(modifier = Modifier
            .padding(end = HalfPadding)
            .constrainAs(options) {
              width = Dimension.wrapContent
              height = Dimension.wrapContent
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
              end.linkTo(parent.end)
            }, onClick = { overflowMenuState = !overflowMenuState }) {
            Icon(
              painter = painterResource(id = R.drawable.ic_more),
              tint = WhiteVariant,
              contentDescription = null,
            )
            PopupMenu(overflowMenuState, homeViewModel, stop, navController) {
              overflowMenuState = false
            }
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
    DropdownMenuItem(onClick = {
      onMenuDismiss.invoke()
//      homeViewModel.setOrigin(stop)
    }, text = {
      Text(
        text = stringResource(id = R.string.action_set_from),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    })
    DropdownMenuItem(onClick = {
      onMenuDismiss.invoke()
//      homeViewModel.setDestination(stop)
    }, text = {
      Text(
        text = stringResource(id = R.string.action_set_to),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    })
    DropdownMenuItem(onClick = {
      navController.navigate(route = "${NavigationScreens.PassingRoutesScreen.name}/${stop.id}")
      onMenuDismiss.invoke()
    }, text = {
      Text(
        text = stringResource(id = R.string.action_show),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    })
  }
}

private fun handleRoute(
  result: RouteResult,
  reversed: Boolean,
  context: Context,
  polylineAnnotationManager: PolylineAnnotationManager,
  map: MapboxMap,
  pointAnnotationManager: PointAnnotationManager,
  navController: NavController,
) {
  when (result) {
    is RouteResult.Success -> {
      val coordinates = result.transport.run {
        if (reversed) stops else stopsReversed
      }.flatMap { it.coordinates }

      createRoute(context, coordinates, polylineAnnotationManager)

      val padding = context.getDimenInt(R.dimen.fab_margin).toDouble()
      val center =
        coordinates.getOrNull(coordinates.lastIndex / 2)?.run { Point.fromLngLat(lng, lat) }
          ?: Point.fromLngLat(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)

      map.easeTo(
        cameraOptions = CameraOptions.Builder().zoom(11.0)
          .padding(EdgeInsets(padding, padding, padding, padding)).center(center).build(),
        animationOptions = MapAnimationOptions.mapAnimationOptions {
          duration(200)
          interpolator(FastOutSlowInInterpolator())
        },
      )

      context.getBitmapFromVectorDrawable(R.drawable.ic_stop_sign)?.let { iconBitmap ->
        val points = coordinates.map { location ->
          PointAnnotationOptions().withPoint(Point.fromLngLat(location.lng, location.lat))
            .withData(location.parentStop.toApiStop().toJson()).withIconSize(STOP_ICON_SIZE)
            .withIconImage(iconBitmap)
        }
        pointAnnotationManager.create(points)
      }
    }
    is RouteResult.Failed -> {
      context.showToast(result.message)
      navController.popBackStack()
    }
  }
}

private fun createRoute(
  context: Context,
  coordinates: List<StopLocation>,
  polylineAnnotationManager: PolylineAnnotationManager,
) {
  val points = coordinates.map { Point.fromLngLat(it.lng, it.lat) }
  val colorRes = R.color.colorAccent300

  val options =
    PolylineAnnotationOptions().withLineColor(context.getColorFromRes(colorRes)).withLineWidth(5.0)
      .withLineJoin(LineJoin.ROUND).withGeometry(LineString.fromLngLats(points))

  polylineAnnotationManager.create(options)
}