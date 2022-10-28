package robert.findtransport.presentation.compose.reusables.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import robert.findtransport.presentation.compose.reusables.HalfPadding
import robert.findtransport.presentation.compose.reusables.TextTitle

@Composable
fun TextPrimary(
  modifier: Modifier = Modifier,
  text: String,
  color: Color = MaterialTheme.colors.onSurface,
  fontSize: TextUnit = TextTitle,
  textAlign: TextAlign = TextAlign.Start,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = fontSize,
    fontWeight = FontWeight.Bold,
    color = color,
    textAlign = textAlign,
  )
}

@Composable
fun TextPrimary(
  modifier: Modifier = Modifier,
  text: AnnotatedString,
  color: Color = MaterialTheme.colors.onSurface,
  fontSize: TextUnit = TextTitle,
  textAlign: TextAlign = TextAlign.Start,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = fontSize,
    fontWeight = FontWeight.Bold,
    color = color,
    textAlign = textAlign,
  )
}
