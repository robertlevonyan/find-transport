package robert.findtransport.presentation.reusables.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import robert.findtransport.R

@Composable
fun A2bAlertDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {},
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = { TextPrimary(text = title) },
        text = { TextSecondary(text = text, textAlign = TextAlign.Start) },
        confirmButton = {
            RegularButton(text = stringResource(id = R.string.label_yes), onClick = onConfirm)
        },
        dismissButton = {
            BlankButton(
                text = stringResource(id = R.string.label_no),
                onClick = onDismiss
            )
        },
        onDismissRequest = onDismissRequest,
    )
}