package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.MenuVerticalOffset

@Composable
fun PopupMenu(
  showMenu: Boolean,
  onOriginSelected: () -> Unit,
  onDestinationSelected: () -> Unit,
  onPassingRoutesClick: () -> Unit,
  onMenuDismiss: () -> Unit,
) {
  DropdownMenu(
    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    expanded = showMenu,
    offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
    onDismissRequest = { onMenuDismiss.invoke() },
  ) {
    DropdownMenuItem(onClick = {
      onOriginSelected()
      onMenuDismiss.invoke()
    }, text = {
      Text(
        text = stringResource(id = R.string.action_set_from),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    })
    DropdownMenuItem(onClick = {
      onDestinationSelected()
      onMenuDismiss.invoke()
    }, text = {
      Text(
        text = stringResource(id = R.string.action_set_to),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    })
    DropdownMenuItem(onClick = {
      onPassingRoutesClick()
      onMenuDismiss.invoke()
    }, text = {
      Text(
        text = stringResource(id = R.string.action_show),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    })
  }
}
