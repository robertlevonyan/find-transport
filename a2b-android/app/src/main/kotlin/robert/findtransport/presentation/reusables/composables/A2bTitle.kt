package robert.findtransport.presentation.reusables.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.Text20
import robert.findtransport.presentation.reusables.theme.ToolbarSize

@Composable
fun A2bTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier
                .size(ToolbarSize)
                .clip(CircleShape)
                .clickable { },
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontWeight = FontWeight.SemiBold,
            fontSize = Text20,
            fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
        )
    }
}
