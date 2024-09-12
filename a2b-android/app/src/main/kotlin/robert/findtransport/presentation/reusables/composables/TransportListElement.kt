package robert.findtransport.presentation.reusables.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import robert.findtransport.presentation.reusables.theme.BarIconSize
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.IconSize
import robert.findtransport.presentation.reusables.theme.SmallPadding
import robert.findtransport.presentation.reusables.theme.TextTransportNumber
import robert.findtransport.presentation.reusables.theme.TransportNumberSize
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getIcon
import robert.findtransport.utils.extensions.getNameFormatted

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.TransportListElement(
    transport: Transport,
    locale: String,
    onElementClick: (Transport) -> Unit,
) {
    val icon = transport.getIcon()
    if (transport == Transport.EMPTY || transport.stops.isEmpty() || transport.type == TransportType.UNDEFINED) return

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onElementClick.invoke(transport) }
        .padding(vertical = SmallPadding)
        .animateItemPlacement()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(fraction = 0.9f)
                .wrapContentHeight()
        ) {
            val (transportIcon, transportNumber, startIcon, firstStop, endIcon, lastStop) = createRefs()

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
                    .width(width = TransportNumberSize)
                    .padding(start = HalfPadding)
                    .constrainAs(transportNumber) {
                        start.linkTo(transportIcon.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                text = transport.getNameFormatted(),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black,
                fontSize = TextTransportNumber,
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            )

            val stops = transport.stops
            val first = stops.first()
            val last = stops.last()

            Image(
                painter = painterResource(id = R.drawable.ic_start_point),
                contentDescription = null,
                modifier = Modifier
                    .size(IconSize)
                    .constrainAs(startIcon) {
                        start.linkTo(transportNumber.end)
                        end.linkTo(firstStop.start)
                        top.linkTo(firstStop.top)
                        bottom.linkTo(firstStop.bottom)
                    },
            )
            TextSecondary(
                modifier = Modifier
                    .constrainAs(firstStop) {
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                        start.linkTo(startIcon.end)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(lastStop.top)
                    },
                text = first.getCurrentName(locale),
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Image(
                painter = painterResource(id = R.drawable.ic_end_point),
                contentDescription = null,
                modifier = Modifier
                    .size(IconSize)
                    .constrainAs(endIcon) {
                        start.linkTo(transportNumber.end)
                        end.linkTo(lastStop.start)
                        top.linkTo(lastStop.top)
                        bottom.linkTo(lastStop.bottom)
                    },
            )
            TextTertiary(
                modifier = Modifier
                    .constrainAs(lastStop) {
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                        start.linkTo(endIcon.end)
                        end.linkTo(parent.end)
                        top.linkTo(firstStop.bottom)
                        bottom.linkTo(parent.bottom)
                    },
                text = last.getCurrentName(locale),
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}
