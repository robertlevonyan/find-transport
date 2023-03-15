package robert.findtransport.presentation.screens.transports

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
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
    itemsIndexed(
      items = transports,
      itemContent = { index, item ->
        item ?: return@itemsIndexed

        TransportListElement(
          transport = item,
          locale = locale,
          onElementClick = onTransportClick,
        )

        if (index < transports.itemCount - 1) {
          Divider(
            color = colorVariantInvertTransparent(),
            thickness = 0.5.dp,
          )
        }
      },
    )
  }
}
