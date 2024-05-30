package robert.findtransport.presentation.reusables.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.presentation.reusables.theme.Shapes

@Composable
fun BlankButton(
  modifier: Modifier = Modifier,
  text: String,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier,
    onClick = onClick,
    shape = Shapes.small,
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onPrimary,
    )
  ) {
    Text(
      text = text,
      fontWeight = FontWeight.Bold,
      fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
    )
  }
}
