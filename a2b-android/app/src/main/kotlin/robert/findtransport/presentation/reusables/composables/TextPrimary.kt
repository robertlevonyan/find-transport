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
import androidx.compose.ui.unit.TextUnit
import robert.findtransport.presentation.reusables.theme.HalfPadding

@Composable
fun TextPrimary(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: TextUnit = MaterialTheme.typography.displayLarge.fontSize,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier.padding(HalfPadding),
        text = text,
        fontSize = fontSize,
        fontWeight = MaterialTheme.typography.displayLarge.fontWeight,
        fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
    )
}

@Composable
fun TextPrimary(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: TextUnit = MaterialTheme.typography.displayLarge.fontSize,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        modifier = modifier.padding(HalfPadding),
        text = text,
        fontSize = fontSize,
        fontWeight = MaterialTheme.typography.displayLarge.fontWeight,
        fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
        color = color,
        textAlign = textAlign,
    )
}
