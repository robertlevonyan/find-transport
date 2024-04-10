package robert.findtransport.presentation.reusables.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.R
import robert.findtransport.presentation.reusables.Text20

@Composable
fun A2bTitle() {
  Text(
    text = stringResource(id = R.string.app_name),
    fontWeight = FontWeight.SemiBold,
    fontSize = Text20,
    fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
  )
}
