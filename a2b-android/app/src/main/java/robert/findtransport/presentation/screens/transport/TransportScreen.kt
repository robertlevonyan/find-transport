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
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopWithAddress
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
import robert.findtransport.presentation.reusables.composables.getMapStyle
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.map.LocationPickerViewModel
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
      TransportContent(
        modifier = modifier,
        transport = transport,
        locale = locale,
        showOptions = showOptions,
        homeViewModel = homeViewModel,
        transportViewModel = transportViewModel,
        navController = navController,
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        scope = scope,
      )
    },
  ) {
    MapContent(
      modifier = Modifier.fillMaxSize(),
      navController = navController,
      locale = locale,
      locationEnabled = locationEnabled,
      mapStyle = mapStyle,
      transport = transport,
      transportViewModel = transportViewModel,
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
