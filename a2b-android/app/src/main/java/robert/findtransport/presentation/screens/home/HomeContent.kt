package robert.findtransport.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.DoublePadding
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.SearchElementSize
import robert.findtransport.presentation.screens.data.CheckDataScreen
import robert.findtransport.presentation.screens.home.components.*
import robert.findtransport.presentation.screens.search.SearchOpenInitiator
import robert.findtransport.utils.EMPTY_ID
import robert.findtransport.utils.extensions.showToast

@Composable
fun HomeContent(
  modifier: Modifier,
  navController: NavController,
  homeViewModel: HomeViewModel,
) {
  val focusManager = LocalFocusManager.current
  val context = LocalContext.current

  val origin by homeViewModel.originLabel.collectAsState()
  val destination by homeViewModel.destinationLabel.collectAsState()
  val locationEnabled by homeViewModel.locationEnabled.collectAsState()
  val showRate by homeViewModel.showRate.collectAsState()

  val originAddress = homeViewModel.origin.collectAsState()
  val destinationAddress = homeViewModel.destination.collectAsState()

  val originStop = homeViewModel.originStop.collectAsState()
  val destinationStop = homeViewModel.destinationStop.collectAsState()

  if (locationEnabled) {
    homeViewModel.getCurrentLocation()
  }

  ConstraintLayout(modifier = modifier) {
    val (appBar, dataCheck, fromCard, swap, toCard, search, allTransports, rate) = createRefs()
    HomeAppBar(
      modifier = Modifier.constrainAs(appBar) {
        start.linkTo(parent.start)
        top.linkTo(parent.top)
        end.linkTo(parent.end)
      },
      containerColor = Color.Transparent,
      onHistoryButtonClicked = { navController.navigate(NavigationScreens.HistoryScreen.name) },
      onSettingsScreenClicked = { navController.navigate(NavigationScreens.SettingsScreen.name) },
      onFeedbackScreenClicked = { navController.navigate(NavigationScreens.FeedbackScreen.name) }
    )

    CheckDataScreen(modifier = Modifier.constrainAs(dataCheck) {
      width = Dimension.fillToConstraints
      height = Dimension.wrapContent
      top.linkTo(appBar.bottom)
      start.linkTo(parent.start)
      end.linkTo(parent.end)
    }) {
      // vpn
    }

    AnimatedVisibility(
      modifier = Modifier
        .padding(FabPadding)
        .constrainAs(rate) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(dataCheck.bottom)
          start.linkTo(parent.start)
          end.linkTo(parent.end)
          bottom.linkTo(fromCard.top)
        },
      visible = showRate,
    ) {
      RateDialog(
        onPositiveClick = homeViewModel::openRate,
        onNegativeClick = homeViewModel::dismissRate,
      )
    }

    SearchInput(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .wrapContentHeight()
        .padding(bottom = FabPadding)
        .constrainAs(fromCard) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          bottom.linkTo(swap.top)
        },
      label = R.string.label_from_long,
      hint = R.string.hint_from,
      trailingIcon = painterResource(id = R.drawable.ic_location_start),
      text = origin.orEmpty(),
      onDropdownClick = {
        navController.navigate(route = "${NavigationScreens.StopsPickerScreen.name}/true") {
          launchSingleTop = true
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
      onTrailingIconClick = {
        navController.navigate(route = "${NavigationScreens.LocationPicker.name}/${0}")
      },
    )

    SwapButton(
      modifier = Modifier.constrainAs(swap) {
        end.linkTo(parent.end)
        bottom.linkTo(toCard.top)
      },
      onClick = { homeViewModel.swap() },
    )

    SearchInput(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .wrapContentHeight()
        .padding(bottom = FabPadding)
        .constrainAs(toCard) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          bottom.linkTo(search.top)
        },
      label = R.string.label_to_long,
      hint = R.string.hint_to,
      trailingIcon = painterResource(id = R.drawable.ic_location),
      text = destination.orEmpty(),
      onDropdownClick = {
        navController.navigate(route = "${NavigationScreens.StopsPickerScreen.name}/false") {
          launchSingleTop = true
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
      onTrailingIconClick = {
        navController.navigate(route = "${NavigationScreens.LocationPicker.name}/${1}")
      },
    )

    SearchButton(
      modifier = Modifier
        .height(SearchElementSize)
        .constrainAs(search) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          bottom.linkTo(allTransports.top)
        },
      onClick = {
        if (origin == null) {
          context.showToast(R.string.error_no_from)
          return@SearchButton
        }
        if (destination == null) {
          context.showToast(R.string.error_no_to)
          return@SearchButton
        }
        if (origin == destination) {
          context.showToast(R.string.error_same_stops)
          return@SearchButton
        }
        val originLatitude = originAddress.value?.latitude?.toFloat()
          ?: originStop.value?.coordinates?.firstOrNull()?.lat ?: return@SearchButton
        val originLongitude = originAddress.value?.longitude?.toFloat()
          ?: originStop.value?.coordinates?.firstOrNull()?.lng ?: return@SearchButton
        val destinationLatitude = destinationAddress.value?.latitude?.toFloat()
          ?: destinationStop.value?.coordinates?.firstOrNull()?.lat ?: return@SearchButton
        val destinationLongitude = destinationAddress.value?.longitude?.toFloat()
          ?: destinationStop.value?.coordinates?.firstOrNull()?.lng ?: return@SearchButton

        val navigationRoute = buildString {
          append("${NavigationScreens.SearchScreen.name}?")
          append("origin_name=${origin}")
          append("&origin_latitude=${originLatitude}")
          append("&origin_longitude=${originLongitude}")
          append("&origin_stop_id=${originStop.value?.id ?: EMPTY_ID}")
          append("&destination_name=${destination}")
          append("&destination_latitude=${destinationLatitude}")
          append("&destination_longitude=${destinationLongitude}")
          append("&destination_stop_id=${destinationStop.value?.id ?: EMPTY_ID}")
          append("&opened=${SearchOpenInitiator.HOME.name}")
        }
        navController.navigate(route = navigationRoute)
      },
    )

    AllTransportsButton(
      modifier = Modifier
        .padding(horizontal = DoublePadding)
        .padding(vertical = FabPadding)
        .constrainAs(allTransports) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          bottom.linkTo(parent.bottom)
        },
      onClick = { navController.navigate(NavigationScreens.TransportsScreen.name) },
    )
  }
}
