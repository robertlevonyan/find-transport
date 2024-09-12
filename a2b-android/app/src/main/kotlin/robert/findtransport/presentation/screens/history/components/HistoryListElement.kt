package robert.findtransport.presentation.screens.history.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import robert.findtransport.R
import robert.findtransport.data.model.History
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.Shapes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.HistoryListElement(
    history: History,
    onSwipe: (History) -> Unit,
    onItemClick: (History) -> Unit,
    onDeleteClick: (History) -> Unit,
) {
    val deleteSwipeAction = SwipeAction(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null,
                tint = Color.Black,
            )
        },
        isUndo = true,
        background = colorResource(id = R.color.colorRemoveRed),
        onSwipe = { onSwipe(history) },
    )

    Card(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = FabPadding, vertical = HalfPadding),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        SwipeableActionsBox(endActions = listOf(deleteSwipeAction)) {
            HistoryItem(
                history = history,
                onItemClick = { onItemClick(history) },
                onDeleteClick = { onDeleteClick(history) },
            )
        }
    }
}