package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.presentation.reusables.BlackVariant
import robert.findtransport.presentation.reusables.BottomPaddingWithFab
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.IconSize
import robert.findtransport.presentation.reusables.RouteWidth
import robert.findtransport.presentation.reusables.Shapes
import robert.findtransport.presentation.reusables.Text13
import robert.findtransport.presentation.reusables.WhiteVariant
import robert.findtransport.utils.extensions.getCurrentName

@Composable
inline fun LastStopCard(
    stop: Stop,
    locale: String,
    showOptions: Boolean,
    crossinline onPassingRoutesClick: (Stop) -> Unit,
    crossinline onOriginSelected: (Stop) -> Unit,
    crossinline onDestinationSelected: (Stop) -> Unit,
) {
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
                            onOriginSelected = { onOriginSelected(stop) },
                            onDestinationSelected = { onDestinationSelected(stop) },
                            onPassingRoutesClick = { onPassingRoutesClick(stop) },
                        ) { overflowMenuState = false }
                    }
                }
            }
        }
    }
}
