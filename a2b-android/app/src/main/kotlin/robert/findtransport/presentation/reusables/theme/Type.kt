package robert.findtransport.presentation.reusables.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.R

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.dejavu)),
        fontSize = TextTitle,
        fontWeight = FontWeight.Bold,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.dejavu)),
        fontSize = Text12,
        fontWeight = FontWeight.Medium,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.dejavu)),
        fontSize = Text10,
        fontWeight = FontWeight.Normal,
    )
)
