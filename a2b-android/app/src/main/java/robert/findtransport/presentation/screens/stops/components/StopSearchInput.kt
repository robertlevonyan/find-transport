package robert.findtransport.presentation.screens.stops.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import robert.findtransport.R
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.Shapes
import robert.findtransport.presentation.reusables.searchInputBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopSearchInput(onValueChange: (String) -> Unit) {
  var inputText by rememberSaveable { mutableStateOf("") }

  OutlinedTextField(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = HalfPadding)
      .padding(bottom = HalfPadding),
    value = inputText,
    onValueChange = {
      inputText = it
      onValueChange.invoke(it)
    },
    singleLine = true,
    shape = Shapes.medium,
    label = {
      Text(
        text = stringResource(id = R.string.hint_search),
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
  )
}
