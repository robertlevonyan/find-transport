package robert.findtransport.presentation.screens.home

import android.app.Activity
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.enums.MapType
import robert.findtransport.data.model.enums.NearbyStopStatus
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.BlankButton
import robert.findtransport.presentation.reusables.composables.RegularButton
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.search.SearchOpenInitiator
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.showToast

@Composable
fun HomeContent(
  modifier: Modifier,
  navController: NavController,
  homeViewModel: HomeViewModel,
) {
  val focusManager = LocalFocusManager.current
  val context = LocalContext.current

  val locale by homeViewModel.locale.collectAsState()
  val selectedFromStop by homeViewModel.fromStop.collectAsState()
  val selectedToStop by homeViewModel.toStop.collectAsState()
  val showRate by homeViewModel.showRate.collectAsState()
  val nearbyStop by homeViewModel.nearbyStop.collectAsState(NearbyStopStatus.Idle)
  val toInput = selectedToStop.getCurrentName(locale)
  var fromInput = remember { selectedFromStop.getCurrentName(locale) }
  var resourceId = remember { R.drawable.ic_current_location_default }

  ConstraintLayout(modifier = modifier) {
    val (fromCard, swap, toCard, search, allTransports,
      rate) = createRefs()

    AnimatedVisibility(
      modifier = Modifier
        .padding(FabPadding)
        .constrainAs(rate) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(parent.start)
          end.linkTo(parent.end)
          bottom.linkTo(fromCard.top)
        },
      visible = showRate
    ) {
      Card {
        Column(
          modifier = Modifier
            .padding(HalfPadding)
            .fillMaxWidth()
        ) {
          TextSecondary(
            text = stringResource(id = R.string.message_rate),
            textAlign = TextAlign.Start,
          )

          Row(modifier = Modifier.align(Alignment.End)) {
            val activity = LocalActivity.current
            RegularButton(text = stringResource(id = R.string.label_yes)) {
              homeViewModel.openRate()
              rate(activity)
            }
            BlankButton(text = stringResource(id = R.string.label_no)) {
              homeViewModel.dismissRate()
            }
          }
        }
      }
    }

    var animatingJob: Job? = null
    when (nearbyStop) {
      NearbyStopStatus.Failed, NearbyStopStatus.Idle -> resourceId = R.drawable.ic_current_location_default
      NearbyStopStatus.Loading ->
        LaunchedEffect(key1 = null) {
          animatingJob = launch {
            while (nearbyStop == NearbyStopStatus.Loading) {
              delay(100)
              resourceId = if (resourceId == R.drawable.ic_current_location_default) {
                R.drawable.ic_current_location_color
              } else {
                R.drawable.ic_current_location_default
              }
            }
          }
        }
      is NearbyStopStatus.NearbyStop -> {
        animatingJob?.cancel()
        resourceId = R.drawable.ic_current_location_color
        val foundNearbyStop = (nearbyStop as NearbyStopStatus.NearbyStop).stop
        homeViewModel.setFromStop(foundNearbyStop)
        fromInput = foundNearbyStop.getCurrentName(locale)
      }
    }

    val topIcon = painterResource(id = resourceId)

    SearchInput(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .wrapContentHeight()
        .padding(bottom = DoublePadding)
        .constrainAs(fromCard) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          bottom.linkTo(swap.top)
        },
      label = R.string.label_from_long,
      hint = R.string.hint_from,
      trailingIcon = topIcon,
      text = fromInput,
      onDropdownClick = {
        navController.navigate(route = "${NavigationScreens.StopsPickerScreen.name}/true") {
          launchSingleTop = true
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
      onTrailingIconClick = {
        navController.navigate(route = "${NavigationScreens.ChooserMapScreen.name}/${MapType.CHOOSER.ordinal}")
      },
    )

    FloatingActionButton(
      modifier = Modifier
        .size(SmallFabSize)
        .padding(bottom = FabPadding)
        .padding(end = FabPadding)
        .constrainAs(swap) {
          end.linkTo(parent.end)
          bottom.linkTo(toCard.top)
        },
      containerColor = MaterialTheme.colorScheme.surface,
      shape = Shapes.medium,
      onClick = { homeViewModel.swap() },
    ) {
      Icon(painter = painterResource(id = R.drawable.ic_swap), contentDescription = null)
    }

    SearchInput(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .wrapContentHeight()
        .padding(bottom = DoublePadding)
        .constrainAs(toCard) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          bottom.linkTo(search.top)
        },
      label = R.string.label_to_long,
      hint = R.string.hint_to,
      trailingIcon = painterResource(id = R.drawable.ic_map),
      text = toInput,
      onDropdownClick = {
        navController.navigate(route = "${NavigationScreens.StopsPickerScreen.name}/false") {
          launchSingleTop = true
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
      onTrailingIconClick = {
        navController.navigate(route = "${NavigationScreens.ChooserMapScreen.name}/${MapType.CHOOSER.ordinal}")
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
        if (selectedFromStop == Stop.EMPTY) {
          context.showToast(R.string.error_no_from)
          return@SearchButton
        }
        if (selectedToStop == Stop.EMPTY) {
          context.showToast(R.string.error_no_to)
          return@SearchButton
        }
        navController.navigate(
          route = NavigationScreens.SearchScreen.name +
              "?from_id=${selectedFromStop.id}&to_id=${selectedToStop.id}&opened=${SearchOpenInitiator.HOME.name}"
        )
      },
    )

    AllTransportsButton(
      modifier = Modifier
        .padding(all = DoublePadding)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchInput(
  modifier: Modifier,
  @StringRes label: Int,
  @StringRes hint: Int,
  trailingIcon: Painter,
  text: String = "",
  keyboardOptions: KeyboardOptions,
  keyboardActions: KeyboardActions,
  onDropdownClick: () -> Unit,
  onTrailingIconClick: () -> Unit,
) {
  Column(modifier = modifier) {
    Text(
      modifier = Modifier.padding(HalfPadding),
      text = stringResource(id = label),
      fontWeight = FontWeight.W600,
      fontSize = Text20,
    )

    Card(
      shape = Shapes.medium,
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
      colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.surface),
    ) {
      Box(
        modifier = Modifier.clickable { onDropdownClick.invoke() },
      ) {
        TextField(
          modifier = Modifier
            .fillMaxWidth()
            .padding(end = BarIconSize)
            .padding(start = SmallPadding)
            .padding(vertical = SmallPadding),
          value = text,
          onValueChange = {},
          label = { Text(text = stringResource(id = hint)) },
          trailingIcon = {
            IconButton(onClick = { onDropdownClick.invoke() }) {
              Icon(painter = painterResource(id = R.drawable.ic_arrow_drop_down), contentDescription = null)
            }
          },
          singleLine = true,
          shape = Shapes.medium,
          colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = searchInputBackgroundColor(),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurface,
          ),
          keyboardOptions = keyboardOptions,
          keyboardActions = keyboardActions,
          readOnly = true,
          enabled = false,
          textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily(Font(R.font.google_sans_regular)),
          ),
        )

        IconButton(
          modifier = Modifier
            .size(BarIconSize)
            .align(Alignment.CenterEnd),
          onClick = { onTrailingIconClick.invoke() },
        ) {
          Icon(painter = trailingIcon, tint = Color.Unspecified, contentDescription = null)
        }
      }
    }
  }
}

@Composable
private fun SearchButton(
  modifier: Modifier,
  onClick: () -> Unit,
) {
  ExtendedFloatingActionButton(
    modifier = modifier,
    icon = {
      Image(
        painter = painterResource(id = R.drawable.ic_search_colored),
        contentDescription = stringResource(id = R.string.label_search),
      )
    },
    text = {
      Text(
        text = stringResource(id = R.string.label_search),
        fontWeight = FontWeight.W400,
      )
    },
    shape = Shapes.medium,
    containerColor = MaterialTheme.colorScheme.surface,
    onClick = { onClick.invoke() },
  )
}

@Composable
private fun AllTransportsButton(
  modifier: Modifier,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier,
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    shape = Shapes.medium,
    onClick = { onClick.invoke() },
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = HalfPadding)
        .padding(bottom = SmallPadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(painter = painterResource(id = R.drawable.ic_arrow_up), contentDescription = null)
      Text(text = stringResource(id = R.string.label_all_transports))
    }
  }
}

private fun rate(activity: Activity) {
  val reviewManager = ReviewManagerFactory.create(activity)
  val requestReviewFlow = reviewManager.requestReviewFlow()
  requestReviewFlow.addOnCompleteListener { request ->
    if (request.isSuccessful) {
      val reviewInfo = request.result
      val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
      flow.addOnCompleteListener {
        if (it.isSuccessful) {
          Log.d("Rate: ", request.result.toString())
        } else {
          Log.e("Error: ", it.exception.toString())
        }
      }
    } else {
      Log.e("Error: ", request.exception.toString())
    }
  }
}
