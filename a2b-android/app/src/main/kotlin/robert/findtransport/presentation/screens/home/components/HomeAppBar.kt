package robert.findtransport.presentation.screens.home.components

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.presentation.reusables.composables.A2bTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    onHistoryButtonClicked: () -> Unit,
    onSettingsScreenClicked: () -> Unit,
    onFeedbackScreenClicked: () -> Unit,
) {
    var overflowMenuState by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = { A2bTitle() },
        actions = {
            IconButton(onClick = onHistoryButtonClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = stringResource(id = R.string.action_history),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(onClick = onFeedbackScreenClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_feedback),
                    contentDescription = stringResource(id = R.string.action_feedback),
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
            HomeOptionsMenu(
                overflowMenuState = overflowMenuState,
                onSettingsScreenClicked = onSettingsScreenClicked,
            ) { overflowMenuState = false }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
        windowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Bottom)
    )
}
