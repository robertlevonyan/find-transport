package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.screens.picker.components.FeedbackButton
import robert.findtransport.presentation.screens.transport.components.MapComponent

@Composable
fun MapContent(
    modifier: Modifier,
    locale: String,
    locationEnabled: Boolean,
    transport: Transport,
    isPrimary: Boolean,
    onBackClick: () -> Unit,
    onFeedbackClick: () -> Unit,
) {
    Box(modifier = modifier) {
        MapComponent(
            locale = locale,
            locationEnabled = locationEnabled,
            transport = transport,
            isPrimary = isPrimary
        )

        SmallFloatingActionButton(modifier = Modifier.padding(
            vertical = FabPadding, horizontal = HalfPadding
        ),
            containerColor = MaterialTheme.colorScheme.secondary,
            onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.label_close),
            )
        }
        FeedbackButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onFeedbackClick,
        )
    }
}
