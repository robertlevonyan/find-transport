package robert.findtransport.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.MenuVerticalOffset
import robert.findtransport.presentation.reusables.Text20
import robert.findtransport.presentation.screens.data.CheckDataScreen
import robert.findtransport.utils.extensions.openPrivacyPolicy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  navController: NavController,
  homeViewModel: HomeViewModel,
) {
  Scaffold { contentPadding ->
    Box(Modifier.padding(contentPadding)) {
      CheckDataScreen(modifier = Modifier.align(Alignment.TopCenter)) {
        // vpn
      }

      HomeContent(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        homeViewModel = homeViewModel,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
  modifier: Modifier = Modifier,
  navController: NavController,
  containerColor: Color = MaterialTheme.colorScheme.background,
) {
  var overflowMenuState by rememberSaveable { mutableStateOf(false) }

  TopAppBar(
    modifier = modifier,
    title = { A2bTitle() },
    actions = {
      IconButton(onClick = { navController.navigate(NavigationScreens.HistoryScreen.name) }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_history),
          contentDescription = stringResource(id = R.string.action_history),
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
      IconButton(onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more),
          contentDescription = stringResource(id = R.string.action_settings),
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
      OptionsMenu(
        overflowMenuState = overflowMenuState,
        navController = navController,
      ) { overflowMenuState = false }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
  )
}

@Composable
private fun A2bTitle() {
  Text(
    text = stringResource(id = R.string.app_name),
    fontWeight = FontWeight.SemiBold,
    fontSize = Text20,
    fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
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
    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    expanded = overflowMenuState,
    offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
    onDismissRequest = { onMenuDismiss.invoke() },
  ) {
    DropdownMenuItem(
      onClick = {
        navController.navigate(NavigationScreens.SettingsScreen.name)
        onMenuDismiss.invoke()
      },
      text = {
        Text(
          text = stringResource(id = R.string.action_settings),
          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        )
      })
    DropdownMenuItem(
      onClick = {
        navController.navigate(NavigationScreens.FeedbackScreen.name)
        onMenuDismiss.invoke()
      },
      text = {
        Text(
          text = stringResource(id = R.string.action_feedback),
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
