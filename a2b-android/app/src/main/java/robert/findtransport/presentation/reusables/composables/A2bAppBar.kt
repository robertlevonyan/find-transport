package robert.findtransport.presentation.reusables.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.presentation.reusables.MenuVerticalOffset
import robert.findtransport.presentation.reusables.SmallPadding
import robert.findtransport.utils.extensions.openPrivacyPolicy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun A2bAppBar(
  title: String,
  @DrawableRes navigationIcon: Int,
  onNavigationIconClick: () -> Unit,
  additionalActions: @Composable RowScope.() -> Unit = {},
) {
  var overflowMenuState by rememberSaveable { mutableStateOf(false) }
  val context = LocalContext.current

  TopAppBar(
    modifier = Modifier.statusBarsPadding(),
    navigationIcon = {
      IconButton(onClick = onNavigationIconClick) {
        Icon(painter = painterResource(id = navigationIcon), contentDescription = null)
      }
    },
    title = {
      TextPrimary(
        modifier = Modifier
          .fillMaxWidth(),
        text = title,
        textAlign = TextAlign.Center,
      )
    },
    actions = {
      additionalActions.invoke(this)

      IconButton(onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more),
          contentDescription = stringResource(id = R.string.action_settings),
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
      DropdownMenu(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        expanded = overflowMenuState,
        offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
        onDismissRequest = { overflowMenuState = false },
      ) {
        DropdownMenuItem(
          onClick = {
            context.openPrivacyPolicy()
            overflowMenuState = false
          },
          text = {
            Text(
              text = stringResource(id = R.string.action_privacy),
              fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            )
          })
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
  )
}
