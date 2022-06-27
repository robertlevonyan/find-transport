package robert.findtransport.presentation.compose.reusables.composables

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.presentation.compose.reusables.Accent
import robert.findtransport.presentation.compose.reusables.Black
import robert.findtransport.presentation.compose.reusables.Shapes

@Composable
fun RegularButton(
  modifier: Modifier = Modifier,
  text: String,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier,
    onClick = onClick,
    shape = Shapes.small,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = Accent,
      contentColor = Black,
    )
  ) {
    Text(
      text = text,
      fontWeight = FontWeight.Bold,
    )
  }
}
