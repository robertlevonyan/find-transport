package robert.findtransport.presentation.screens.transports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.reusables.colorVariantInvertTransparent
import robert.findtransport.presentation.reusables.composables.TransportListElement
import robert.findtransport.presentation.screens.transports.components.TransportTypeSelector

@Composable
fun TransportsContent(
  modifier: Modifier,
  transports: LazyPagingItems<Transport>,
  locale: String,
  transportCategory: TransportCategory,
  onTransportCategoryClick: (TransportCategory) -> Unit,
  onTransportClick: (Transport) -> Unit,
) {
  LazyColumn(modifier = modifier) {
    item { TransportTypeSelector(transportCategory, onTransportCategoryClick) }
    items(
      count = transports.itemCount,
      key = transports.itemKey { it.id },
      contentType = transports.itemContentType { it.number },
    ) { index ->
      val item = transports[index]
      if (item == null) {
        Box(modifier = Modifier.size(0.dp)) {}
      } else {
        TransportListElement(
          transport = item,
          locale = locale,
          onElementClick = onTransportClick,
        )
        if (index < transports.itemCount - 1) {
          HorizontalDivider(
            color = colorVariantInvertTransparent(),
            thickness = 0.5.dp,
          )
        }
      }
    }
  }
}
