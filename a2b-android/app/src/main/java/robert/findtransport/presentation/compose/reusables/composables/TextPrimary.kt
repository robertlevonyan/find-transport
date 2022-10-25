package robert.findtransport.presentation.compose.reusables.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.presentation.compose.reusables.HalfPadding

@Composable
fun TextPrimary(
  modifier: Modifier = Modifier,
  text: String,
  color: Color = MaterialTheme.colors.onSurface,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = robert.findtransport.presentation.compose.reusables.TextTitle,
    fontWeight = FontWeight.Bold,
    color = color,
  )
}
