package robert.findtransport.presentation.screens.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.screens.settings.components.GeneralSettings
import robert.findtransport.presentation.screens.settings.components.LanguageSetting
import robert.findtransport.presentation.screens.settings.components.ThemeSetting
import robert.findtransport.presentation.screens.settings.components.UpdateSetting
import robert.findtransport.utils.LNG_AM
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU

@Composable
fun SettingsContent(modifier: Modifier, settingsViewModel: SettingsViewModel) {
    val currentContext = LocalContext.current
    val currentLanguage by settingsViewModel.locale.collectAsState()
    val theme by settingsViewModel.theme.collectAsState()
    val checking by settingsViewModel.loaded.collectAsState()

    LazyColumn(modifier = modifier) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                LanguageSetting(
                    modifier = Modifier.align(Alignment.Center),
                    currentLanguage = currentLanguage,
                ) { position: Int ->
                    when (position) {
                        0 -> settingsViewModel.changeLanguage(LNG_AM)
                        1 -> settingsViewModel.changeLanguage(LNG_EN)
                        2 -> settingsViewModel.changeLanguage(LNG_RU)
                    }
                    if (currentContext is Activity) {
                        currentContext.recreate()
                    }
                }
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = FabPadding)
            ) {
                ThemeSetting(
                    modifier = Modifier.align(Alignment.Center),
                    theme = theme,
                ) { position: Int ->
                    when (position) {
                        0 -> settingsViewModel.changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
                        1 -> settingsViewModel.changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
                        2 -> settingsViewModel.changeTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = FabPadding)
            ) {
                UpdateSetting(
                    modifier = Modifier.align(Alignment.Center),
                    checking = checking,
                    onSettingClick = settingsViewModel::checkForUpdate,
                )
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = FabPadding)
            ) {
                GeneralSettings(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
