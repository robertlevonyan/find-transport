package robert.findtransport.presentation.screens.picker.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.Black
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding

@Composable
fun BoxScope.CurrentLocationButton(onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .navigationBarsPadding()
            .padding(bottom = HalfPadding)
            .padding(horizontal = FabPadding),
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = Black,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_current_location),
            contentDescription = stringResource(id = R.string.cd_current_location),
            tint = Black,
        )
    }
}
