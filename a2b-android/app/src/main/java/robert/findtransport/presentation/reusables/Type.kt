package robert.findtransport.presentation.reusables

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.R

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = FontFamily(Font(R.font.google_sans_regular)),
    fontSize = TextTitle,
    fontWeight = FontWeight.Bold,
  ),
  displayMedium = TextStyle(
    fontFamily = FontFamily(Font(R.font.google_sans_regular)),
    fontSize = Text13,
    fontWeight = FontWeight.Medium,
  ),
  displaySmall = TextStyle(
    fontFamily = FontFamily(Font(R.font.google_sans_regular)),
    fontSize = Text11,
    fontWeight = FontWeight.Normal,
  )
)
