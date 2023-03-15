package robert.findtransport.presentation.screens.transports.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.composables.TextSecondary

@Composable
fun TransportTypeBus(
  modifier: Modifier,
  transportCategory: TransportCategory,
  onTransportCategoryClick: (TransportCategory) -> Unit,
) {
  Card(modifier = modifier.padding(HalfPadding)) {
    Column(modifier = Modifier
      .fillMaxWidth()
      .clickable {
        if (transportCategory != TransportCategory.BUS) {
          onTransportCategoryClick.invoke(TransportCategory.BUS)
        }
      }) {
      TextSecondary(
        modifier = Modifier
          .padding(horizontal = HalfPadding)
          .padding(top = HalfPadding), text = stringResource(id = R.string.label_bus)
      )

      Image(
        modifier = Modifier
          .size(56.dp)
          .align(alignment = Alignment.End),
        painter = painterResource(id = R.drawable.ic_bus_outline),
        contentDescription = null,
      )
    }
  }
}
