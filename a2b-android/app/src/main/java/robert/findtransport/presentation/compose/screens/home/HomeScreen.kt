package robert.findtransport.presentation.compose.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import robert.findtransport.R
import robert.findtransport.presentation.compose.reusables.BarIconSize
import robert.findtransport.presentation.compose.reusables.MenuVerticalOffset
import robert.findtransport.presentation.compose.reusables.backgroundColor
import robert.findtransport.presentation.compose.reusables.backgroundColorVariant
import robert.findtransport.presentation.compose.screens.data.CheckDataScreen

@Composable
fun HomeScreen(navController: NavController) {
  Scaffold(topBar = { HomeAppBar() }) { contentPadding ->
    Box(Modifier.padding(contentPadding)) {
      CheckDataScreen(modifier = Modifier.align(Alignment.TopCenter)) {
        // vpn
      }

      SearchScreen(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
      )
    }
  }
}

@Composable
fun HomeAppBar() {
  var overflowMenuState by rememberSaveable { mutableStateOf(false) }

  TopAppBar(
    modifier = Modifier.statusBarsPadding(),
    title = { A2bTitle() },
    actions = {
      IconButton(onClick = { }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_history),
          contentDescription = stringResource(id = R.string.action_history),
          tint = Color.Unspecified,
        )
      }
      IconButton(onClick = { overflowMenuState = !overflowMenuState }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_more_vertical),
          contentDescription = stringResource(id = R.string.action_settings),
          tint = Color.Unspecified,
        )
      }
      OptionsMenu(overflowMenuState) { overflowMenuState = false }
    },
    backgroundColor = backgroundColor(),
    elevation = 0.dp,
  )
}

@Composable
fun A2bTitle() {
  Row(verticalAlignment = Alignment.CenterVertically) {
    IconButton(onClick = { }) {
      Icon(
        modifier = Modifier.size(BarIconSize),
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(id = R.string.app_name),
        tint = Color.Unspecified,
      )
    }
    Text(
      modifier = Modifier.systemBarsPadding(false),
      text = stringResource(id = R.string.app_name),
      fontWeight = FontWeight.Bold,
    )
  }
}

@Composable
fun OptionsMenu(overflowMenuState: Boolean, onMenuDismiss: () -> Unit) {
  DropdownMenu(
    modifier = Modifier.background(backgroundColorVariant()),
    expanded = overflowMenuState,
    offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
    onDismissRequest = { onMenuDismiss.invoke() },
  ) {
    DropdownMenuItem(onClick = { onMenuDismiss.invoke() }) {
      Text(text = stringResource(id = R.string.action_settings))
    }
    DropdownMenuItem(onClick = { onMenuDismiss.invoke() }) {
      Text(text = stringResource(id = R.string.action_feedback))
    }
    DropdownMenuItem(onClick = { onMenuDismiss.invoke() }) {
      Text(text = stringResource(id = R.string.action_privacy))
    }
  }
}
