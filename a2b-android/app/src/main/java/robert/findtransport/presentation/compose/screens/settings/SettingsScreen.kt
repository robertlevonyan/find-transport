package robert.findtransport.presentation.compose.screens.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.robertlevonyan.compose.buttontogglegroup.IconPosition
import com.robertlevonyan.compose.buttontogglegroup.RowToggleButtonGroup
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.data.model.enums.DataLoading
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TextMessage
import robert.findtransport.utils.LNG_AM
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.LNG_RU

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
    SettingsList(
      modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      settingsViewModel = settingsViewModel,
    )
  }
}

@Composable
private fun SettingsList(modifier: Modifier, settingsViewModel: SettingsViewModel) {
  LazyColumn(modifier = modifier) {
    item {
      Box(modifier = Modifier.fillMaxWidth()) {
        LanguageSetting(modifier = Modifier.align(Alignment.Center), settingsViewModel = settingsViewModel)
      }
    }
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = FabPadding)
      ) {
        ThemeSetting(modifier = Modifier.align(Alignment.Center), settingsViewModel = settingsViewModel)
      }
    }
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = FabPadding)
      ) {
        UpdateSetting(modifier = Modifier.align(Alignment.Center), settingsViewModel = settingsViewModel)
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

@Composable
private fun LanguageSetting(modifier: Modifier, settingsViewModel: SettingsViewModel) {
  val currentContext = LocalContext.current
  val currentLanguage by settingsViewModel.locale.collectAsState()
  val selectionIndex = when (currentLanguage) {
    LNG_AM -> 0
    LNG_EN -> 1
    else -> 2
  }

  Column(
    modifier = modifier
      .fillMaxWidth(fraction = 0.9f)
      .wrapContentHeight()
  ) {
    TextMessage(text = stringResource(id = R.string.settings_language))

    RowToggleButtonGroup(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .align(Alignment.CenterHorizontally),
      buttonCount = 3,
      onButtonClick = { position ->
        when (position) {
          0 -> settingsViewModel.changeLanguage(LNG_AM)
          1 -> settingsViewModel.changeLanguage(LNG_EN)
          2 -> settingsViewModel.changeLanguage(LNG_RU)
        }
        if (currentContext is Activity) {
          currentContext.recreate()
        }
      },
      buttonTexts = arrayOf(
        stringResource(id = R.string.settings_language_am_short),
        stringResource(id = R.string.settings_language_en_short),
        stringResource(id = R.string.settings_language_ru_short),
      ),
      buttonIcons = arrayOf(
        painterResource(id = R.drawable.ic_lng_arm),
        painterResource(id = R.drawable.ic_lng_eng),
        painterResource(id = R.drawable.ic_lng_rus),
      ),
      buttonIconTint = Color.Transparent,
      unselectedButtonIconTint = Color.Transparent,
      primarySelection = selectionIndex,
      selectedColor = Color(integerArrayResource(id = R.array.colors_bg)[0]),
      iconPosition = IconPosition.Top,
      unselectedColor = MaterialTheme.colors.background,
      shape = Shapes.large,
    )
  }
}

@Composable
private fun ThemeSetting(modifier: Modifier, settingsViewModel: SettingsViewModel) {
  val theme by settingsViewModel.theme.collectAsState()
  val selectionIndex = when (theme) {
    AppCompatDelegate.MODE_NIGHT_NO -> 0
    AppCompatDelegate.MODE_NIGHT_YES -> 1
    else -> 2
  }

  Column(
    modifier = modifier
      .fillMaxWidth(fraction = 0.9f)
      .wrapContentHeight()
  ) {
    TextMessage(text = stringResource(id = R.string.settings_theme))

    RowToggleButtonGroup(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .align(Alignment.CenterHorizontally),
      buttonCount = 3,
      onButtonClick = { position ->
        when (position) {
          0 -> settingsViewModel.changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
          1 -> settingsViewModel.changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
          2 -> settingsViewModel.changeTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
      },
      buttonTexts = arrayOf(
        stringResource(id = R.string.settings_theme_light),
        stringResource(id = R.string.settings_theme_dark),
        stringResource(id = R.string.settings_theme_system_short),
      ),
      buttonIcons = arrayOf(
        painterResource(id = R.drawable.ic_theme_day),
        painterResource(id = R.drawable.ic_theme_night),
        painterResource(id = R.drawable.ic_theme_system),
      ),
      unselectedButtonIconTint = MaterialTheme.colors.onSurface,
      primarySelection = selectionIndex,
      selectedColor = Color(integerArrayResource(id = R.array.colors_bg)[1]),
      iconPosition = IconPosition.Top,
      unselectedColor = MaterialTheme.colors.background,
      shape = Shapes.large,
    )
  }
}

@Composable
private fun UpdateSetting(modifier: Modifier, settingsViewModel: SettingsViewModel) {
  val checking by settingsViewModel.loaded.collectAsState()
  Column(
    modifier = modifier
      .fillMaxWidth(fraction = 0.9f)
      .wrapContentHeight()
  ) {
    TextMessage(text = stringResource(id = R.string.settings_check_database))

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      elevation = 0.dp,
      shape = Shapes.large,
      backgroundColor = Color(integerArrayResource(id = R.array.colors_bg)[2]),
    ) {

      val textLabel = when (checking) {
        DataLoading.Loading -> R.string.message_check_download
        is DataLoading.Failed -> R.string.error_not_downloaded
        else -> R.string.settings_update_database
      }

      Column(modifier = Modifier.clickable { settingsViewModel.checkForUpdate() }) {
        TextMessage(
          modifier = Modifier
            .align(Alignment.Start)
            .wrapContentSize()
            .padding(HalfPadding),
          text = stringResource(id = textLabel),
          color = BlackVariant,
        )
        AnimatedVisibility(
          modifier = Modifier.fillMaxWidth(),
          visible = checking == DataLoading.Loading
        ) {
          LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = BlackVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun GeneralSettings(modifier: Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth(fraction = 0.9f)
      .wrapContentHeight()
  ) {
    TextMessage(text = stringResource(id = R.string.settings_general))

    Row(
      modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth(fraction = 0.47f)
          .height(GeneralSettingCardSize),
        elevation = 0.dp,
        shape = Shapes.large,
        backgroundColor = Color(integerArrayResource(id = R.array.colors_bg)[3]),
      ) {
        Column(
          modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
        ) {
          Image(
            modifier = modifier
              .fillMaxWidth()
              .padding(FabPadding),
            painter = painterResource(id = R.drawable.ic_star_half),
            contentDescription = null,
            alignment = Alignment.Center,
          )

          Text(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .wrapContentSize(),
            text = stringResource(id = R.string.settings_rate),
            color = BlackVariant,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          )

          TextMessage(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .wrapContentSize()
              .padding(bottom = FabPadding),
            text = stringResource(id = R.string.settings_rate_details),
            color = BlackVariant,
          )
        }
      }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(GeneralSettingCardSize)
          .padding(start = FabPadding),
        elevation = 0.dp,
        shape = Shapes.large,
        backgroundColor = Color(integerArrayResource(id = R.array.colors_bg)[4]),
      ) {
        Column(
          modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
        ) {
          Image(
            modifier = modifier
              .fillMaxWidth()
              .padding(FabPadding),
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            alignment = Alignment.Center,
          )

          Text(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .wrapContentSize(),
            text = stringResource(id = R.string.settings_app_version),
            color = BlackVariant,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          )

          TextMessage(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .wrapContentSize()
              .padding(bottom = FabPadding),
            text = BuildConfig.VERSION_NAME,
            color = BlackVariant,
          )
        }
      }
    }
  }
}
