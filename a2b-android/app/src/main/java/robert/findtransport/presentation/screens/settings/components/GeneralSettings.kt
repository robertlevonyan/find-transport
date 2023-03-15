package robert.findtransport.presentation.screens.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.presentation.reusables.BlackVariant
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.GeneralSettingCardSize
import robert.findtransport.presentation.reusables.Shapes
import robert.findtransport.presentation.reusables.composables.TextSecondary

@Composable
fun GeneralSettings(modifier: Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth(fraction = 0.9f)
      .wrapContentHeight()
  ) {
    TextSecondary(text = stringResource(id = R.string.settings_general))

    Row(
      modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth(fraction = 0.47f)
          .height(GeneralSettingCardSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(integerArrayResource(id = R.array.colors_bg)[3])),
        shape = Shapes.medium,
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
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            textAlign = TextAlign.Center,
          )

          TextSecondary(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .wrapContentSize(),
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(integerArrayResource(id = R.array.colors_bg)[4])),
        shape = Shapes.medium,
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
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            textAlign = TextAlign.Center,
          )

          TextSecondary(
            modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .wrapContentSize(),
            text = BuildConfig.VERSION_NAME,
            color = BlackVariant,
          )
        }
      }
    }
  }
}
