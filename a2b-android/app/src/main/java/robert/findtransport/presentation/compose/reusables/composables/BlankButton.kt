package robert.findtransport.presentation.compose.reusables.composables

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.presentation.compose.reusables.Shapes

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
      backgroundColor = MaterialTheme.colors.primary,
      contentColor = MaterialTheme.colors.onPrimary,
    )
  ) {
    Text(
      text = text,
      fontWeight = FontWeight.Bold,
    )
  }
}
