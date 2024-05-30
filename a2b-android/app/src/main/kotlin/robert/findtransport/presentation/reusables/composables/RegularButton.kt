package robert.findtransport.presentation.reusables.composables

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.presentation.reusables.theme.Accent
import robert.findtransport.presentation.reusables.theme.Black
import robert.findtransport.presentation.reusables.theme.Shapes

@Composable
fun RegularButton(
  modifier: Modifier = Modifier,
  text: String,
  colors: ButtonColors = ButtonDefaults.buttonColors(
    containerColor = Accent,
    contentColor = Black,
  ),
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier,
    onClick = onClick,
    shape = Shapes.small,
    colors = colors,
  ) {
    Text(
      text = text,
      fontWeight = FontWeight.Bold,
      fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
    )
  }
}
