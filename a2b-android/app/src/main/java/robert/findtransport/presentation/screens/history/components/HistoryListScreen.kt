package robert.findtransport.presentation.screens.history.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.data.model.History
import robert.findtransport.presentation.reusables.Black
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.composables.A2bAlertDialog

@Composable
fun HistoryListScreen(
  modifier: Modifier,
  history: List<History>,
  onRemoveHistoryClicked: (History) -> Unit,
  onClearHistoryClicked: () -> Unit,
  onRestoreHistoryClicked: (History) -> Unit,
) {
  var showClearDialog by rememberSaveable { mutableStateOf(false) }
  var showRestoreDialog by rememberSaveable { mutableStateOf(false) }
  var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
  var selectedHistoryItem by remember { mutableStateOf<History?>(null) }

  Box(modifier = modifier) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .animateContentSize()
    ) {
      items(history) { history ->
        HistoryListElement(
          history = history,
          onSwipe = { historyItem ->
            selectedHistoryItem = historyItem
            showDeleteDialog = true
          },
          onItemClick = { historyItem ->
            selectedHistoryItem = historyItem
            showRestoreDialog = !showRestoreDialog
          },
          onDeleteClick = { historyItem ->
            selectedHistoryItem = historyItem
            showDeleteDialog = true
          },
        )
      }
    }

    FloatingActionButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(FabPadding),
      containerColor = MaterialTheme.colorScheme.secondary,
      contentColor = Black,
      onClick = { showClearDialog = !showClearDialog }
    ) {
      Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = null)
    }

    if (showClearDialog) {
      A2bAlertDialog(
        title = stringResource(id = R.string.title_history),
        text = stringResource(id = R.string.message_history_dialog_clear),
        onConfirm = {
          onClearHistoryClicked()
          showClearDialog = false
        },
        onDismiss = { showClearDialog = false },
      ) { showClearDialog = false }
    }

    if (showRestoreDialog) {
      A2bAlertDialog(
        title = stringResource(id = R.string.title_history),
        text = stringResource(id = R.string.message_history_dialog_restore),
        onConfirm = {
          showRestoreDialog = false
          val historyItem = selectedHistoryItem ?: return@A2bAlertDialog
          onRestoreHistoryClicked(historyItem)
        },
        onDismiss = { showRestoreDialog = false },
      ) { showRestoreDialog = false }
    }

    if (showDeleteDialog) {
      A2bAlertDialog(
        title = stringResource(id = R.string.title_history),
        text = stringResource(id = R.string.message_history_dialog_delete),
        onConfirm = {
          showDeleteDialog = false
          val historyItem = selectedHistoryItem ?: return@A2bAlertDialog
          onRemoveHistoryClicked(historyItem)
        },
        onDismiss = { showDeleteDialog = false },
      ) { showDeleteDialog = false }
    }
  }
}
