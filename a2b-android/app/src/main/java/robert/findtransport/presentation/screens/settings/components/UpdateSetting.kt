package robert.findtransport.presentation.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.data.model.enums.DataLoading
import robert.findtransport.presentation.reusables.BlackVariant
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.Shapes
import robert.findtransport.presentation.reusables.composables.TextSecondary

@Composable
fun UpdateSetting(
  modifier: Modifier,
  checking: DataLoading,
  onSettingClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .fillMaxWidth(fraction = 0.9f)
      .wrapContentHeight()
  ) {
    TextSecondary(text = stringResource(id = R.string.settings_check_database))

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
      colors = CardDefaults.cardColors(containerColor = Color(integerArrayResource(id = R.array.colors_bg)[2])),
      shape = Shapes.medium,
    ) {
      val textLabel = when (checking) {
        DataLoading.Loading -> R.string.message_check_download
        is DataLoading.Failed -> R.string.error_not_downloaded
        else -> R.string.settings_update_database
      }

      Column(modifier = Modifier.clickable { onSettingClick() }) {
        TextSecondary(
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
