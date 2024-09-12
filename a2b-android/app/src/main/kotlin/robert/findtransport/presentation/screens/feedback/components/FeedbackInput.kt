package robert.findtransport.presentation.screens.feedback.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.Shapes
import robert.findtransport.presentation.reusables.theme.searchInputBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackInput(
    modifier: Modifier = Modifier,
    @StringRes hint: Int,
    text: String,
    singleLine: Boolean = true,
    error: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    requestFocus: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = HalfPadding, horizontal = FabPadding)
            .focusRequester(focusRequester),
        value = text,
        onValueChange = onValueChange,
        singleLine = singleLine,
        shape = Shapes.medium,
        label = {
            Text(
                text = stringResource(id = hint),
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = searchInputBackgroundColor(),
            focusedBorderColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.surface,
            disabledBorderColor = MaterialTheme.colorScheme.surface,
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
        ),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        ),
        isError = error != -1,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
    )

    if (requestFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}
