package robert.findtransport.presentation.screens.home.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.BarIconSize
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.Shapes
import robert.findtransport.presentation.reusables.theme.SmallPadding
import robert.findtransport.presentation.reusables.theme.Text20
import robert.findtransport.presentation.reusables.theme.searchInputBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInput(
  modifier: Modifier,
  @StringRes label: Int,
  @StringRes hint: Int,
  trailingIcon: Painter,
  text: String = "",
  keyboardOptions: KeyboardOptions,
  keyboardActions: KeyboardActions,
  onDropdownClick: () -> Unit,
  onTrailingIconClick: () -> Unit,
) {
  Column(modifier = modifier) {
    Text(
      modifier = Modifier.padding(HalfPadding),
      text = stringResource(id = label),
      fontWeight = FontWeight.W600,
      fontSize = Text20,
      fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
    )

    Card(
      shape = Shapes.medium,
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
      colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.surface),
    ) {
      Box(
        modifier = Modifier.clickable { onDropdownClick.invoke() },
      ) {
        TextField(
          modifier = Modifier
            .fillMaxWidth()
            .padding(end = BarIconSize)
            .padding(start = SmallPadding)
            .padding(vertical = SmallPadding),
          value = text,
          onValueChange = {},
          label = {
            Text(
              text = stringResource(id = hint),
              fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            )
          },
          trailingIcon = {
            IconButton(onClick = { onDropdownClick.invoke() }) {
              Icon(
                painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                contentDescription = null
              )
            }
          },
          singleLine = true,
          shape = Shapes.medium,
          colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = searchInputBackgroundColor(),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurface,
          ),
          keyboardOptions = keyboardOptions,
          keyboardActions = keyboardActions,
          readOnly = true,
          enabled = false,
          textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
          ),
        )

        IconButton(
          modifier = Modifier
            .size(BarIconSize)
            .align(Alignment.CenterEnd),
          onClick = { onTrailingIconClick.invoke() },
        ) {
          Icon(
            painter = trailingIcon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null
          )
        }
      }
    }
  }
}
