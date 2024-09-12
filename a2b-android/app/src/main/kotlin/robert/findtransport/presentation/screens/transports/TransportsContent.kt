package robert.findtransport.presentation.screens.transports

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.reusables.composables.TransportListElement
import robert.findtransport.presentation.reusables.theme.colorVariantInvertTransparent
import robert.findtransport.presentation.screens.transports.components.TransportTypeSelector

@Composable
fun TransportsContent(
    modifier: Modifier,
    transports: List<Transport>,
    locale: String,
    transportCategory: TransportCategory,
    onTransportCategoryClick: (TransportCategory) -> Unit,
    onTransportClick: (Transport) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        item { TransportTypeSelector(transportCategory, onTransportCategoryClick) }
        itemsIndexed(transports) { index, item ->
            TransportListElement(
                transport = item,
                locale = locale,
                onElementClick = onTransportClick,
            )
            val thickness = if (index < transports.lastIndex) 0.5.dp else 0.dp
            HorizontalDivider(
                color = colorVariantInvertTransparent(),
                thickness = thickness,
            )
        }
    }
}
