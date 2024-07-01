package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.reusables.theme.BarIconSize
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.IconSize
import robert.findtransport.presentation.reusables.theme.Text12
import robert.findtransport.presentation.reusables.theme.Text24
import robert.findtransport.presentation.reusables.theme.TextTransportNumber
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getIcon
import robert.findtransport.utils.extensions.getNameFormatted

@Composable
inline fun TransportInfo(
  transport: Transport,
  isPrimary: Boolean,
  locale: String,
  crossinline onSwapClick: () -> Unit,
  crossinline onElementClick: () -> Unit,
) {
  ConstraintLayout(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .background(color = MaterialTheme.colorScheme.secondary)
      .padding(horizontal = FabPadding)
      .clickable { onElementClick.invoke() }
  ) {
    val icon = transport.getIcon()

    val (transportIcon, transportNumber, startIcon, firstStop, endIcon, lastStop, stopCount, swap) = createRefs()

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
    if (stops.isEmpty()) return@ConstraintLayout

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

    IconButton(
      modifier = Modifier
        .constrainAs(swap) {
          end.linkTo(stopCount.start)
          top.linkTo(stopCount.top)
          bottom.linkTo(stopCount.bottom)
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
        .constrainAs(startIcon) {
          start.linkTo(parent.start)
          end.linkTo(firstStop.start)
          top.linkTo(firstStop.top)
          bottom.linkTo(firstStop.bottom)
        },
    )
    TextPrimary(
      modifier = Modifier
        .padding(top = FabPadding)
        .constrainAs(firstStop) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(startIcon.end)
          end.linkTo(parent.end)
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
        .constrainAs(endIcon) {
          start.linkTo(parent.start)
          end.linkTo(lastStop.start)
          top.linkTo(lastStop.top)
          bottom.linkTo(lastStop.bottom)
        },
    )
    TextSecondary(
      modifier = Modifier
        .constrainAs(lastStop) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(endIcon.end)
          end.linkTo(parent.end)
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
