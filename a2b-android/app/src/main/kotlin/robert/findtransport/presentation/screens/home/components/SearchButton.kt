package robert.findtransport.presentation.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.Shapes

@Composable
fun SearchButton(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_search_colored),
                contentDescription = stringResource(id = R.string.label_search),
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.label_search),
                fontWeight = FontWeight.W400,
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            )
        },
        shape = Shapes.medium,
        containerColor = MaterialTheme.colorScheme.surface,
        onClick = { onClick.invoke() },
    )
}
