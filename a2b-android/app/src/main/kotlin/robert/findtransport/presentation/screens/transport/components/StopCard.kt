package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.RouteWidth
import robert.findtransport.presentation.reusables.SmallIconSize
import robert.findtransport.presentation.reusables.Text11
import robert.findtransport.utils.extensions.getCurrentName

@Composable
inline fun StopCard(
    stop: Stop,
    locale: String,
    showOptions: Boolean,
    crossinline onPassingRoutesClick: (Stop) -> Unit,
    crossinline onOriginSelected: (Stop) -> Unit,
    crossinline onDestinationSelected: (Stop) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(horizontal = FabPadding)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (name, dot, route, options) = createRefs()
        val dotGuide = createGuidelineFromStart(fraction = 0.08f)

        Box(
            modifier = Modifier
                .width(RouteWidth)
                .constrainAs(route) {
                    height = Dimension.fillToConstraints
                    start.linkTo(dot.start)
                    end.linkTo(dot.end)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                }
                .background(MaterialTheme.colorScheme.onPrimary),
        )

        Image(
            modifier = Modifier
                .padding(HalfPadding)
                .size(SmallIconSize)
                .constrainAs(dot) {
                    start.linkTo(dotGuide)
                    end.linkTo(dotGuide)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            painter = rememberAsyncImagePainter(
                ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_route_dot_normal)
            ),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
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
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                },
            text = stop.getCurrentName(locale),
            fontSize = Text11,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            color = MaterialTheme.colorScheme.onPrimary
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
                    tint = MaterialTheme.colorScheme.onPrimary,
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
