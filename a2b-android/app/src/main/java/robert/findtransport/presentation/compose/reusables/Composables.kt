package robert.findtransport.presentation.compose.reusables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getIcon
import robert.findtransport.utils.extensions.getTypeName

@Composable
fun A2bAppBar(
  title: String,
  @DrawableRes navigationIcon: Int,
  onNavigationIconClick: () -> Unit,
  additionalActions: @Composable RowScope.() -> Unit = {},
) {
  var overflowMenuState by rememberSaveable { mutableStateOf(false) }

  TopAppBar(
    modifier = Modifier.statusBarsPadding(),
    navigationIcon = {
      IconButton(onClick = onNavigationIconClick) {
        Icon(painter = painterResource(id = navigationIcon), contentDescription = null)
      }
    },
    title = {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = SmallPadding)
          .systemBarsPadding(false),
        text = title,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
    },
    actions = {
      additionalActions.invoke(this)

      IconButton(onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more_vertical),
          contentDescription = stringResource(id = R.string.action_settings),
          tint = Color.Unspecified,
        )
      }
      DropdownMenu(
        modifier = Modifier.background(colorVariant()),
        expanded = overflowMenuState,
        offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
        onDismissRequest = { overflowMenuState = false },
      ) {
        DropdownMenuItem(onClick = { overflowMenuState = false }) {
          Text(text = stringResource(id = R.string.action_privacy))
        }
      }
    },
    backgroundColor = backgroundColor(),
    elevation = 0.dp,
  )
}

@Composable
fun TransportListElement(
  transport: Transport,
  locale: String,
  onElementClick: (Transport) -> Unit,
  hasStar: Boolean,
  onStarCheckedChange: (Boolean) -> Unit = {},
) {
  val icon = transport.getIcon()
  val type = transport.getTypeName()

  Box(modifier = Modifier
    .fillMaxWidth()
    .clickable { onElementClick.invoke(transport) }
    .padding(vertical = HalfPadding)
  ) {
    ConstraintLayout(
      modifier = Modifier
        .align(Alignment.Center)
        .fillMaxWidth(fraction = 0.9f)
        .wrapContentHeight()
    ) {
      val (transportIcon, transportNumber, transportType, firstLast, star) = createRefs()

      AsyncImage(
        modifier = Modifier
          .size(BarIconSize)
          .constrainAs(transportIcon) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
          },
        model = ImageRequest.Builder(context = LocalContext.current).data(icon).build(),
        contentDescription = null,
      )

      Text(
        modifier = Modifier
          .padding(horizontal = FabPadding)
          .constrainAs(transportNumber) {
            start.linkTo(transportIcon.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
          },
        text = transport.number,
        color = colorVariantInvert(),
        fontWeight = FontWeight.Black,
        fontSize = TextTransportNumber,
      )

      Text(
        modifier = Modifier
          .constrainAs(transportType) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(transportNumber.end)
            end.linkTo(if (hasStar) star.start else parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(firstLast.top)
          },
        text = stringResource(id = type),
        color = colorVariantInvert(),
        fontSize = Text20,
        textAlign = TextAlign.Start,
      )

      val stops = transport.stops
      val first = stops.first()
      val last = stops.last()

      Text(
        modifier = Modifier
          .constrainAs(firstLast) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(transportNumber.end)
            end.linkTo(if (hasStar) star.start else parent.end)
            top.linkTo(transportType.bottom)
            bottom.linkTo(parent.bottom)
          },
        text = "${first.getCurrentName(locale)} - ${last.getCurrentName(locale)}",
        color = colorVariantInvert(),
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        fontSize = Text11,
        maxLines = 1,
      )

      if (hasStar) {
        IconToggleButton(
          modifier = Modifier
            .padding(start = FabPadding)
            .constrainAs(star) {
              end.linkTo(parent.end)
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
            },
          checked = transport.isFavorite,
          onCheckedChange = { checked -> onStarCheckedChange.invoke(checked) },
        ) {
          val iconPainter = if (transport.isFavorite) {
            R.drawable.ic_favorite_filled
          } else {
            R.drawable.ic_favorite_outline
          }
          Icon(painter = painterResource(id = iconPainter), contentDescription = null)
        }
      }
    }
  }
}

@Composable
fun TextTitle(
  modifier: Modifier = Modifier,
  text: String,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = TextTitle,
    fontWeight = FontWeight.Bold,
    color = colorVariantInvert(),
  )
}

@Composable
fun TextMessage(
  modifier: Modifier = Modifier,
  text: String,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = TextMessage,
    fontWeight = FontWeight.Normal,
    color = colorVariantInvert(),
  )
}

@Composable
fun RegularButton(
  modifier: Modifier = Modifier,
  text: String,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier,
    onClick = onClick,
    shape = Shapes.small,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = Accent,
      contentColor = Black,
    )
  ) {
    Text(
      text = text,
      fontWeight = FontWeight.Bold,
    )
  }
}

@Composable
fun ColumnToggleButtonGroup(
  modifier: Modifier = Modifier,
  buttonCount: Int,
  primarySelection: Int = -1,
  selectedColor: Color = Accent,
  unselectedColor: Color = Color.Transparent,
  selectedTextColor: Color = BlackVariant,
  unselectedTextColor: Color = colorVariantInvert(),
  borderColor: Color = selectedColor,
  buttonTexts: Array<String> = Array(buttonCount) { "" },
  shape: CornerBasedShape = Shapes.large,
  borderSize: Dp = 1.dp,
  border: BorderStroke? = BorderStroke(borderSize, borderColor),
  onButtonClick: (index: Int) -> Unit,
) {
  Column(
    modifier = modifier,
  ) {
    val squareCorner = CornerSize(0.dp)
    var selectionIndex by rememberSaveable { mutableStateOf(primarySelection) }

    repeat(buttonCount) { index ->
      val buttonShape = when (index) {
        0 -> shape.copy(bottomStart = squareCorner, bottomEnd = squareCorner)
        buttonCount - 1 -> shape.copy(topStart = squareCorner, topEnd = squareCorner)
        else -> shape.copy(all = squareCorner)
      }
      val isButtonSelected = selectionIndex == index
      val backgroundColor = if (isButtonSelected) selectedColor else unselectedColor
      val textColor = if (isButtonSelected) selectedTextColor else unselectedTextColor

      OutlinedButton(
        modifier = Modifier
          .offset(y = borderSize * -index)
          .height(60.dp)
          .fillMaxWidth(),
        contentPadding = PaddingValues(),
        shape = buttonShape,
        border = border,
        onClick = {
          selectionIndex = index
          onButtonClick.invoke(index)
        },
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = backgroundColor),
      ) {
        Text(
          text = buttonTexts[index],
          color = textColor
        )
      }
    }
  }
}
