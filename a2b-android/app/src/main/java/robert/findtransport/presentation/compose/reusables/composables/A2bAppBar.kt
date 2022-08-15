package robert.findtransport.presentation.compose.reusables.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import robert.findtransport.R
import robert.findtransport.presentation.compose.reusables.MenuVerticalOffset
import robert.findtransport.presentation.compose.reusables.SmallPadding
import robert.findtransport.utils.extensions.openPrivacyPolicy

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
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = SmallPadding)
          .systemBarsPadding(false),
        text = title,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
    },
    actions = {
      additionalActions.invoke(this)

      IconButton(onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more_vertical),
          contentDescription = stringResource(id = R.string.action_settings),
          tint = MaterialTheme.colors.onSurface,
        )
      }
      DropdownMenu(
        modifier = Modifier.background(MaterialTheme.colors.surface),
        expanded = overflowMenuState,
        offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
        onDismissRequest = { overflowMenuState = false },
      ) {
        DropdownMenuItem(onClick = {
          context.openPrivacyPolicy()
          overflowMenuState = false
        }) {
          Text(text = stringResource(id = R.string.action_privacy))
        }
      }
    },
    backgroundColor = MaterialTheme.colors.background,
    elevation = 0.dp,
  )
}
