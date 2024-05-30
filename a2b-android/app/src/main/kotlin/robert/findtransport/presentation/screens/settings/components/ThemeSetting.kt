package robert.findtransport.presentation.screens.settings.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.Shapes
import robert.findtransport.presentation.reusables.composables.IconPosition
import robert.findtransport.presentation.reusables.composables.RowToggleButtonGroup
import robert.findtransport.presentation.reusables.composables.TextSecondary

@Composable
fun ThemeSetting(
  modifier: Modifier,
  theme: Int,
  onSettingClick: (Int) -> Unit,
) {
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
    TextSecondary(text = stringResource(id = R.string.settings_theme))

    RowToggleButtonGroup(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .align(Alignment.CenterHorizontally),
      buttonCount = 3,
      onButtonClick = onSettingClick,
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
      unselectedButtonIconTint = MaterialTheme.colorScheme.onSurface,
      primarySelection = selectionIndex,
      selectedColor = Color(integerArrayResource(id = R.array.colors_bg)[1]),
      iconPosition = IconPosition.Top,
      unselectedColor = MaterialTheme.colorScheme.background,
      shape = Shapes.medium,
    )
  }
}
