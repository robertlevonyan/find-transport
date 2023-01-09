package robert.findtransport.presentation.reusables.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.*
import androidx.compose.material3.*
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
import robert.findtransport.presentation.reusables.*
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getIcon
import robert.findtransport.utils.extensions.getTypeName

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.TransportListElement(
  transport: Transport,
  locale: String,
  onElementClick: (Transport) -> Unit,
  trailingIcon: TransportListElementTrailingIcon = TransportListElementTrailingIcon.NONE,
  onStarCheckedChange: (Boolean) -> Unit = {},
  onTrackClick: () -> Unit = {},
) {
  val icon = transport.getIcon()
  if (transport == Transport.EMPTY || transport.stops.isEmpty() || transport.type == TransportType.UNDEFINED) return

  val type = transport.getTypeName()

  Box(modifier = Modifier
    .animateItemPlacement()
    .fillMaxWidth()
    .clickable { onElementClick.invoke(transport) }
    .padding(vertical = SmallPadding)
  ) {
    ConstraintLayout(
      modifier = Modifier
        .align(Alignment.Center)
        .fillMaxWidth(fraction = 0.9f)
        .wrapContentHeight()
    ) {
      val (transportIcon, transportNumber, transportType, firstLast, trailingIconComposable) = createRefs()

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
          .padding(horizontal = HalfPadding)
          .constrainAs(transportNumber) {
            start.linkTo(transportIcon.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
          },
        text = transport.number,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Black,
        fontSize = TextTransportNumber,
      )

      TextPrimary(
        modifier = Modifier
          .constrainAs(transportType) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(transportNumber.end)
            end.linkTo(if (trailingIcon == TransportListElementTrailingIcon.NONE) trailingIconComposable.start else parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(firstLast.top)
          },
        text = stringResource(id = type),
      )

      val stops = transport.stops
      val first = stops.first()
      val last = stops.last()

      TextTertiary(
        modifier = Modifier
          .constrainAs(firstLast) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            start.linkTo(transportNumber.end)
            end.linkTo(if (trailingIcon == TransportListElementTrailingIcon.NONE) parent.end else trailingIconComposable.start)
            top.linkTo(transportType.bottom)
            bottom.linkTo(parent.bottom)
          },
        text = "${first.getCurrentName(locale)} - ${last.getCurrentName(locale)}",
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
      )

      when (trailingIcon) {
        TransportListElementTrailingIcon.STAR -> IconToggleButton(
          modifier = Modifier
            .padding(start = FabPadding)
            .constrainAs(trailingIconComposable) {
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
        TransportListElementTrailingIcon.TRACK -> IconButton(
          modifier = Modifier
            .padding(start = FabPadding)
            .constrainAs(trailingIconComposable) {
              end.linkTo(parent.end)
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
            },
          onClick = { onTrackClick.invoke() }) {
          Icon(painter = painterResource(id = R.drawable.ic_track_route), contentDescription = null)
        }
        TransportListElementTrailingIcon.NONE -> Unit
      }
    }
  }
}

enum class TransportListElementTrailingIcon {
  NONE, STAR, TRACK
}