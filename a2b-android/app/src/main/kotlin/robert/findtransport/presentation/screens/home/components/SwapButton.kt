package robert.findtransport.presentation.screens.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import robert.findtransport.R
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.Shapes
import robert.findtransport.presentation.reusables.SmallFabSize

@Composable
fun SwapButton(modifier: Modifier, onClick: () -> Unit) {
  FloatingActionButton(
    modifier = modifier
      .padding(top = FabPadding)
      .size(SmallFabSize)
      .padding(bottom = FabPadding)
      .padding(end = FabPadding),
    containerColor = MaterialTheme.colorScheme.surface,
    shape = Shapes.medium,
    onClick = onClick,
  ) {
    Icon(painter = painterResource(id = R.drawable.ic_swap), contentDescription = null)
  }
}
