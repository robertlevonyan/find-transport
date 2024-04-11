package robert.findtransport.presentation.screens.transport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.screens.transport.components.StopList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun TransportContent(
    modifier: Modifier,
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
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
    ) {
        StopList(
            transport = transport,
            locale = locale,
            showOptions = showOptions,
            bottomSheetScaffoldState = bottomSheetScaffoldState,
            lazyColumnState = lazyColumnState,
            isPrimary = isPrimary,
            onPassingRoutesClick = onPassingRoutesClick,
            onSwapClick = onSwapClick,
            onOriginSelected = onOriginSelected,
            onDestinationSelected = onDestinationSelected,
        )
    }
}
