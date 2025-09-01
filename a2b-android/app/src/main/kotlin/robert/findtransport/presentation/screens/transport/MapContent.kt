package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.reusables.composables.A2bAlertDialog
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.screens.picker.components.FeedbackButton
import robert.findtransport.presentation.screens.picker.components.InfoButton
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
    var showInfo by rememberSaveable { mutableStateOf(false) }
    Box(modifier = modifier) {
        MapComponent(
            locale = locale,
            locationEnabled = locationEnabled,
            transport = transport,
            isPrimary = isPrimary
        )

        SmallFloatingActionButton(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = HalfPadding),
            containerColor = MaterialTheme.colorScheme.secondary,
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.label_close),
            )
        }
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopEnd),
        ) {
            InfoButton { showInfo = true }
            FeedbackButton(
                onClick = onFeedbackClick,
            )
        }
    }
    if (showInfo) {
        A2bAlertDialog(
            title = "",
            text = stringResource(R.string.message_info),
            confirmTitle = stringResource(R.string.label_ok),
            onDismissRequest = { showInfo = false },
            onConfirm = { showInfo = false },
        )
    }
}
