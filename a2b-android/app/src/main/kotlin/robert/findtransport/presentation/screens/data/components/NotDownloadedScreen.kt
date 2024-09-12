package robert.findtransport.presentation.screens.data.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.presentation.reusables.composables.RegularButton
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.SmallPadding

@Composable
fun NotDownloadedScreen(onRetry: () -> Unit) {
    Column {
        TextSecondary(text = stringResource(id = R.string.error_not_downloaded))

        RegularButton(
            modifier = Modifier
                .padding(horizontal = HalfPadding)
                .padding(bottom = SmallPadding)
                .align(Alignment.End),
            text = stringResource(id = R.string.label_retry),
            onClick = onRetry,
        )
    }
}
