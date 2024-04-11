package robert.findtransport.presentation.screens.transport.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.presentation.reusables.CornerRadius
import robert.findtransport.presentation.reusables.FabPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun StopList(
    transport: Transport,
    locale: String,
    showOptions: Boolean,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    lazyColumnState: LazyListState,
    isPrimary: Boolean,
    crossinline onPassingRoutesClick: (Stop) -> Unit,
    crossinline onSwapClick: () -> Unit,
    crossinline onOriginSelected: (Stop) -> Unit,
    crossinline onDestinationSelected: (Stop) -> Unit,
) {
    if (transport == Transport.EMPTY) return
    val stops = if (isPrimary) transport.stops else transport.stopsReversed
    val isMetro = transport.type == TransportType.METRO
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyColumnState,
    ) {
        item {
            Card(
                modifier = Modifier.padding(bottom = FabPadding),
                shape = RoundedCornerShape(bottomEnd = CornerRadius, bottomStart = CornerRadius),
            ) {
                TransportInfo(
                    transport = transport,
                    isPrimary = isPrimary,
                    locale = locale,
                    onSwapClick = onSwapClick,
                    onElementClick = {
                        scope.launch {
                            val bottomSheetState = bottomSheetScaffoldState.bottomSheetState
                            when (bottomSheetState.currentValue) {
                                SheetValue.Expanded -> bottomSheetState.partialExpand()
                                SheetValue.PartiallyExpanded -> bottomSheetState.expand()
                                SheetValue.Hidden -> return@launch
                            }
                        }
                    },
                )
            }
        }
        itemsIndexed(stops) { index, stop ->
            when (index) {
                0 -> FirstStopCard(
                    stop = stop,
                    locale = locale,
                    showOptions = showOptions,
                    onPassingRoutesClick = onPassingRoutesClick,
                    onOriginSelected = onOriginSelected,
                    onDestinationSelected = onDestinationSelected,
                )

                stops.lastIndex -> LastStopCard(
                    stop = stop,
                    locale = locale,
                    showOptions = showOptions,
                    onPassingRoutesClick = onPassingRoutesClick,
                    onOriginSelected = onOriginSelected,
                    onDestinationSelected = onDestinationSelected,
                )

                else -> StopCard(
                    stop = stop,
                    locale = locale,
                    showOptions = showOptions,
                    onPassingRoutesClick = onPassingRoutesClick,
                    onOriginSelected = onOriginSelected,
                    onDestinationSelected = onDestinationSelected,
                )
            }
        }
    }
}
