package robert.findtransport.presentation.reusables.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import robert.findtransport.presentation.reusables.HalfPadding

@Composable
fun TextTertiary(
  modifier: Modifier = Modifier,
  text: String,
  color: Color = MaterialTheme.colorScheme.onSurface,
  textAlign: TextAlign = TextAlign.Center,
  overflow: TextOverflow = TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = MaterialTheme.typography.displaySmall.fontSize,
    fontWeight = MaterialTheme.typography.displaySmall.fontWeight,
    color = color,
    textAlign = textAlign,
    overflow = overflow,
    maxLines = maxLines,
  )
}

@Composable
fun TextTertiary(
  modifier: Modifier = Modifier,
  text: AnnotatedString,
  color: Color = MaterialTheme.colorScheme.onSurface,
  textAlign: TextAlign = TextAlign.Center,
  overflow: TextOverflow = TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE,
) {
  Text(
    modifier = modifier.padding(HalfPadding),
    text = text,
    fontSize = MaterialTheme.typography.displaySmall.fontSize,
    fontWeight = MaterialTheme.typography.displaySmall.fontWeight,
    color = color,
    textAlign = textAlign,
    overflow = overflow,
    maxLines = maxLines,
  )
}
