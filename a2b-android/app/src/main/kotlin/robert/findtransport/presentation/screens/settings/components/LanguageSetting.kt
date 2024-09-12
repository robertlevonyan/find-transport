package robert.findtransport.presentation.screens.settings.components

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
import robert.findtransport.utils.LNG_AM
import robert.findtransport.utils.LNG_EN

@Composable
fun LanguageSetting(
    modifier: Modifier,
    currentLanguage: String,
    onSettingClick: (Int) -> Unit,
) {
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
        TextSecondary(text = stringResource(id = R.string.settings_language))

        RowToggleButtonGroup(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally),
            buttonCount = 3,
            onButtonClick = onSettingClick,
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
            unselectedColor = MaterialTheme.colorScheme.background,
            shape = Shapes.medium,
        )
    }
}
