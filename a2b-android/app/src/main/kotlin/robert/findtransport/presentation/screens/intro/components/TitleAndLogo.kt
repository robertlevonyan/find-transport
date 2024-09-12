package robert.findtransport.presentation.screens.intro.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.TextIntroLabel

@Composable
fun TitleAndLogo(modifier: Modifier) {
    Row(modifier = modifier.wrapContentSize()) {
        Image(
            modifier = Modifier
                .scale(0.75f)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
        )
        Text(
            modifier = modifier
                .padding(HalfPadding)
                .align(Alignment.CenterVertically),
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = TextIntroLabel,
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            textAlign = TextAlign.Center,
        )
    }
}
