package robert.findtransport.presentation.reusables.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.DialogBoxSize
import robert.findtransport.presentation.reusables.theme.FabPadding

@Composable
fun A2bDialog(
    title: String,
    text: String,
    image: Painter,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .width(DialogBoxSize)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .wrapContentHeight()
        ) {
            TextPrimary(
                modifier = Modifier.padding(FabPadding),
                text = title,
            )

            TextSecondary(
                modifier = Modifier.padding(horizontal = FabPadding),
                text = text,
            )

            Image(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                painter = image,
                contentDescription = null,
            )
            val bottomModifier = Modifier
                .align(alignment = Alignment.End)
                .padding(FabPadding)

            if (onDismiss == null) {
                RegularButton(
                    modifier = bottomModifier,
                    text = stringResource(id = R.string.label_ok),
                ) {
                    onConfirm.invoke()
                }
            } else {
                Row(modifier = bottomModifier) {
                    RegularButton(text = stringResource(id = R.string.label_ok)) {
                        onConfirm.invoke()
                    }
                    RegularButton(text = stringResource(id = R.string.label_close)) {
                        onDismiss.invoke()
                    }
                }
            }
        }
    }
}