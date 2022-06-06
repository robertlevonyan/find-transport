package robert.findtransport.presentation.compose.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.*

@Composable
fun SearchScreen(
  modifier: Modifier,
  navController: NavController,
) {
  var fromInput by rememberSaveable { mutableStateOf("") }
  var toInput by rememberSaveable { mutableStateOf("") }
  val focusManager = LocalFocusManager.current

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
      onDropdownClick = { navController.navigate(NavigationScreens.StopsPickerScreen.name) },
      onInputChange = { input -> fromInput = input },
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
      backgroundColor = backgroundColorVariant(),
      shape = Shapes.medium,
      onClick = {},
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
      onDropdownClick = { navController.navigate(NavigationScreens.StopsPickerScreen.name) },
      onInputChange = { input -> toInput = input },
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
      onClick = { },
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
  keyboardOptions: KeyboardOptions,
  keyboardActions: KeyboardActions,
  onDropdownClick: () -> Unit,
  onInputChange: (String) -> Unit,
) {
  var textInput by rememberSaveable { mutableStateOf("") }

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
      backgroundColor = backgroundColorVariant(),
    ) {
      Box {
        TextField(
          modifier = Modifier
            .fillMaxWidth()
            .padding(end = BarIconSize)
            .padding(vertical = SmallPadding)
            .background(Color.Transparent),
          value = textInput,
          onValueChange = {
            textInput = it
            onInputChange.invoke(it)
          },
          placeholder = { Text(text = stringResource(id = hint)) },
          trailingIcon = {
            IconButton(onClick = { onDropdownClick.invoke() }) {
              Icon(painter = painterResource(id = R.drawable.ic_arrow_drop_down), contentDescription = null)
            }
          },
          singleLine = true,
          shape = Shapes.medium,
          colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = backgroundColorVariant(),
            focusedBorderColor = backgroundColorVariant(),
            unfocusedBorderColor = backgroundColorVariant(),
            disabledBorderColor = backgroundColorVariant(),
            errorBorderColor = backgroundColorVariant(),
            cursorColor = backgroundColorVariantInvert(),
          ),
          keyboardOptions = keyboardOptions,
          keyboardActions = keyboardActions,
          readOnly = true,
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
    backgroundColor = backgroundColorVariant(),
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
