package robert.findtransport.presentation.compose.reusables.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.presentation.compose.reusables.HalfPadding
import robert.findtransport.presentation.compose.reusables.colorVariantInvert

@Composable
fun TextMessage(
  modifier: Modifier = Modifier,
  text: String,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = robert.findtransport.presentation.compose.reusables.TextMessage,
    fontWeight = FontWeight.Normal,
    color = colorVariantInvert(),
  )
}
