package robert.findtransport.presentation.compose.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.presentation.compose.screens.search.SearchOpenInitiator
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun HomeContent(
  modifier: Modifier,
  navController: NavController,
  homeViewModel: HomeViewModel,
) {
  val focusManager = LocalFocusManager.current

  val locale by homeViewModel.locale.collectAsState()
  val selectedFromStop by homeViewModel.fromStop.collectAsState()
  val selectedToStop by homeViewModel.toStop.collectAsState()
  val fromInput = selectedFromStop.getCurrentName(locale)
  val toInput = selectedToStop.getCurrentName(locale)

  ConstraintLayout(modifier = modifier) {
    val (fromCard, swap, toCard, search, allTransports) = createRefs()

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
      trailingIcon = R.drawable.ic_current_location_black,
      text = fromInput,
      onDropdownClick = {
        navController.navigate(route = "${NavigationScreens.StopsPickerScreen.name}/true") {
          launchSingleTop = true
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(
        onNext = { focusManager.moveFocus(FocusDirection.Down) }
      ),
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
      backgroundColor = MaterialTheme.colors.surface,
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
      trailingIcon = R.drawable.ic_map,
      text = toInput,
      onDropdownClick = {
        navController.navigate(route = "${NavigationScreens.StopsPickerScreen.name}/false") {
          launchSingleTop = true
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(
        onDone = { focusManager.clearFocus() }
      ),
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
        navController.navigate(
          route = NavigationScreens.SearchScreen.name +
              "?from_id=${selectedFromStop.id}&to_id=${selectedToStop.id}&opened=${SearchOpenInitiator.HOME.name}"
        )
      },
    )

    AllTransportsButton(
      modifier = Modifier
        .constrainAs(allTransports) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          bottom.linkTo(parent.bottom)
        }
        .padding(all = DoublePadding),
      onClick = { navController.navigate(NavigationScreens.TransportsScreen.name) },
    )
  }
}

@Composable
fun SearchInput(
  modifier: Modifier,
  @StringRes label: Int,
  @StringRes hint: Int,
  @DrawableRes trailingIcon: Int,
  text: String = "",
  keyboardOptions: KeyboardOptions,
  keyboardActions: KeyboardActions,
  onDropdownClick: () -> Unit,
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
      elevation = 0.dp,
      backgroundColor = MaterialTheme.colors.surface,
    ) {
      Box(
        modifier = Modifier.clickable { onDropdownClick.invoke() },
      ) {
        TextField(
          modifier = Modifier
            .fillMaxWidth()
            .padding(end = BarIconSize)
            .padding(start = SmallPadding)
            .padding(vertical = SmallPadding)
            .background(color = searchInputBackgroundColor(), shape = SearchInputShape),
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
            backgroundColor = searchInputBackgroundColor(),
            focusedBorderColor = MaterialTheme.colors.surface,
            unfocusedBorderColor = MaterialTheme.colors.surface,
            disabledBorderColor = MaterialTheme.colors.surface,
            errorBorderColor = MaterialTheme.colors.surface,
            cursorColor = MaterialTheme.colors.onSurface,
          ),
          keyboardOptions = keyboardOptions,
          keyboardActions = keyboardActions,
          readOnly = true,
          enabled = false,
          textStyle = TextStyle(
            color = MaterialTheme.colors.onSurface,
            fontFamily = FontFamily(Font(R.font.google_sans_regular)),
          )
        )

        IconButton(
          modifier = Modifier
            .size(BarIconSize)
            .align(Alignment.CenterEnd),
          onClick = { },
        ) {
          Icon(painter = painterResource(id = trailingIcon), contentDescription = null)
        }
      }
    }
  }
}

@Composable
fun SearchButton(
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
    backgroundColor = MaterialTheme.colors.surface,
    onClick = { onClick.invoke() },
  )
}

@Composable
fun AllTransportsButton(
  modifier: Modifier,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier,
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    shape = Shapes.large,
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
