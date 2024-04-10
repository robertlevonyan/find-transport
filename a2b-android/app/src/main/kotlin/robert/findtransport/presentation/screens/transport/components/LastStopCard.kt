package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopWithAddress
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.transport.TransportViewModel
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun LastStopCard(
  stop: Stop,
  locale: String,
  showOptions: Boolean,
  homeViewModel: HomeViewModel,
  transportViewModel: TransportViewModel,
  navController: NavController,
) {
  val scope = rememberCoroutineScope()

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
            PopupMenu(
              overflowMenuState,
              onOriginSelected = {
                scope.launch {
                  val address = transportViewModel.getAddress(stop)
                  homeViewModel.setOriginStop(StopWithAddress(stop, address))
                }
              },
              onDestinationSelected = {
                scope.launch {
                  val address = transportViewModel.getAddress(stop)
                  homeViewModel.setDestinationStop(StopWithAddress(stop, address))
                }
              },
              onPassingRoutesClick = {
                navController.navigate(route = "${NavigationScreens.PassingRoutesScreen.name}/${stop.id}")
              },
            ) { overflowMenuState = false }
          }
        }
      }
    }
  }
}
