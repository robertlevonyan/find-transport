package robert.findtransport.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.MenuVerticalOffset
import robert.findtransport.utils.extensions.openPrivacyPolicy

@Composable
fun HomeOptionsMenu(
  overflowMenuState: Boolean,
  onSettingsScreenClicked: () -> Unit,
  onMenuDismiss: () -> Unit,
) {
  val context = LocalContext.current

  DropdownMenu(
    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    expanded = overflowMenuState,
    offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
    onDismissRequest = { onMenuDismiss.invoke() },
  ) {
    DropdownMenuItem(onClick = {
      onSettingsScreenClicked()
      onMenuDismiss.invoke()
    }, text = {
      Text(
        text = stringResource(id = R.string.action_settings),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    })
    DropdownMenuItem(
      onClick = {
        context.openPrivacyPolicy()
        onMenuDismiss.invoke()
      },
      text = {
        Text(
          text = stringResource(id = R.string.action_privacy),
          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        )
      },
    )
  }
}
