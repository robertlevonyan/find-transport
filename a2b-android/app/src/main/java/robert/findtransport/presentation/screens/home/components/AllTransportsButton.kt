package robert.findtransport.presentation.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.Shapes
import robert.findtransport.presentation.reusables.SmallPadding

@Composable
fun AllTransportsButton(
  modifier: Modifier,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier,
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    shape = Shapes.medium,
    onClick = { onClick.invoke() },
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = HalfPadding)
        .padding(bottom = SmallPadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(painter = painterResource(id = R.drawable.ic_arrow_up), contentDescription = null)
      Text(
        text = stringResource(id = R.string.label_all_transports),
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
      )
    }
  }
}
