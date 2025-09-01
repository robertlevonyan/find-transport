package robert.findtransport.presentation.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.A2bAppBar

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            A2bAppBar(
                title = stringResource(id = R.string.title_settings),
                navigationIcon = R.drawable.ic_arrow_back,
                onNavigationIconClick = { navController.popBackStack() },
            )
        }
    ) { contentPadding ->
        SettingsContent(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            settingsViewModel = settingsViewModel,
        )
    }
}
