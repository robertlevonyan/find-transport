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
    confirmTitle: String = stringResource(id = R.string.label_yes),
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = title.takeIf { it.isNotEmpty() }?.let { { TextPrimary(text = title) } },
        text = { TextSecondary(text = text, textAlign = TextAlign.Start) },
        confirmButton = {
            RegularButton(text = confirmTitle, onClick = onConfirm)
        },
        dismissButton = onDismiss?.let {
            {
                BlankButton(
                    text = stringResource(id = R.string.label_no),
                    onClick = it,
                )
            }
        },
        onDismissRequest = onDismissRequest,
    )
}
