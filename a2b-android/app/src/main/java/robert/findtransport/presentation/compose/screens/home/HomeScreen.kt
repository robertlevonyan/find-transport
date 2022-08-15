package robert.findtransport.presentation.compose.screens.home

import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import robert.findtransport.R
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.MenuVerticalOffset
import robert.findtransport.presentation.compose.reusables.Text24
import robert.findtransport.presentation.compose.screens.data.CheckDataScreen
import robert.findtransport.utils.extensions.getColorFromRes
import robert.findtransport.utils.extensions.openPrivacyPolicy
import robert.findtransport.utils.extensions.showToast

@Composable
fun HomeScreen(
  navController: NavController,
  homeViewModel: HomeViewModel,
) {
  Scaffold(topBar = { HomeAppBar(navController = navController) }) { contentPadding ->
    Box(Modifier.padding(contentPadding)) {
      CheckDataScreen(modifier = Modifier.align(Alignment.TopCenter)) {
        // vpn
      }

      SearchScreen(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        homeViewModel = homeViewModel
      )
    }
  }
}

@Composable
fun HomeAppBar(navController: NavController) {
  var overflowMenuState by rememberSaveable { mutableStateOf(false) }

  TopAppBar(
    modifier = Modifier.statusBarsPadding(),
    title = { A2bTitle() },
    actions = {
      IconButton(onClick = { navController.navigate(NavigationScreens.HistoryScreen.name) }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_history),
          contentDescription = stringResource(id = R.string.action_history),
          tint = MaterialTheme.colors.onSurface,
        )
      }
      IconButton(onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more_vertical),
          contentDescription = stringResource(id = R.string.action_settings),
          tint = MaterialTheme.colors.onSurface,
        )
      }
      OptionsMenu(
        overflowMenuState = overflowMenuState,
        navController = navController,
      ) { overflowMenuState = false }
    },
    backgroundColor = MaterialTheme.colors.background,
    elevation = 0.dp,
  )
}

@Composable
private fun A2bTitle() {
  Text(
    modifier = Modifier.systemBarsPadding(false),
    text = stringResource(id = R.string.app_name),
    fontWeight = FontWeight.Bold,
    fontSize = Text24,
  )
}

@Composable
private fun OptionsMenu(
  overflowMenuState: Boolean,
  navController: NavController,
  onMenuDismiss: () -> Unit,
) {
  val context = LocalContext.current

  DropdownMenu(
    modifier = Modifier.background(MaterialTheme.colors.surface),
    expanded = overflowMenuState,
    offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
    onDismissRequest = { onMenuDismiss.invoke() },
  ) {
    DropdownMenuItem(onClick = {
      navController.navigate(NavigationScreens.SettingsScreen.name)
      onMenuDismiss.invoke()
    }) {
      Text(text = stringResource(id = R.string.action_settings))
    }
    DropdownMenuItem(onClick = {
      navController.navigate(NavigationScreens.FeedbackScreen.name)
      onMenuDismiss.invoke()
    }) {
      Text(text = stringResource(id = R.string.action_feedback))
    }
    DropdownMenuItem(onClick = {
      context.openPrivacyPolicy()
      onMenuDismiss.invoke()
    }) {
      Text(text = stringResource(id = R.string.action_privacy))
    }
  }
}
