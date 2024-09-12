package robert.findtransport.presentation.screens.search.components

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import robert.findtransport.data.model.RouteSearchResult
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.reusables.composables.TransportListElement

@Composable
fun LazyItemScope.TransportElement(
    multiRouteElement: RouteSearchResult,
    locale: String,
    onClick: (Transport) -> Unit,
) {
    val transport = multiRouteElement.transport ?: Transport.EMPTY
    TransportListElement(
        transport = transport,
        locale = locale,
        onElementClick = onClick
    )
}
