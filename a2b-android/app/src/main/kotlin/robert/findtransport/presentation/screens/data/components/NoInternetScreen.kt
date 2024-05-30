package robert.findtransport.presentation.screens.data.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.SmallPadding
import robert.findtransport.presentation.reusables.composables.RegularButton
import robert.findtransport.presentation.reusables.composables.TextSecondary

@Composable
fun NoInternetScreen() {
  val context = LocalContext.current

  Column {
    TextSecondary(text = stringResource(id = R.string.error_no_internet))

    RegularButton(
      modifier = Modifier
        .padding(horizontal = HalfPadding)
        .padding(bottom = SmallPadding)
        .align(Alignment.End),
      text = stringResource(id = R.string.label_open_settings),
      onClick = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
    )
  }
}
