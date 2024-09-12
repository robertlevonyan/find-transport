package robert.findtransport.presentation.screens.picker.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding

@Composable
fun FeedbackButton(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    SmallFloatingActionButton(
        modifier = modifier.padding(
            vertical = FabPadding,
            horizontal = HalfPadding,
        ),
        containerColor = MaterialTheme.colorScheme.secondary,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_feedback),
            contentDescription = stringResource(id = R.string.action_feedback),
        )
    }
}
