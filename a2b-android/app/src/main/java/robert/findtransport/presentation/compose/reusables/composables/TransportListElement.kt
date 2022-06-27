package robert.findtransport.presentation.compose.reusables.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getIcon
import robert.findtransport.utils.extensions.getTypeName

@Composable
fun TransportListElement(
  transport: Transport,
  locale: String,
  onElementClick: (Transport) -> Unit,
  hasStar: Boolean,
  onStarCheckedChange: (Boolean) -> Unit = {},
) {
  val icon = transport.getIcon()
  if (transport.type == TransportType.UNDEFINED) return

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
