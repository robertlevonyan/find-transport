package robert.findtransport.presentation.compose.screens.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.History
import robert.findtransport.presentation.compose.reusables.FabPadding
import robert.findtransport.presentation.compose.reusables.HalfPadding
import robert.findtransport.presentation.compose.reusables.Shapes
import robert.findtransport.presentation.compose.reusables.composables.A2bAlertDialog
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TextMessage
import robert.findtransport.utils.extensions.format
import robert.findtransport.utils.extensions.getCurrentName
import java.util.*

@Composable
fun HistoryScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  historyViewModel: HistoryViewModel = hiltViewModel(),
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_history),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
      )
    }
  ) { contentPadding ->
    HistoryContent(
      modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      historyViewModel = historyViewModel,
    )
  }
}

@Composable
private fun HistoryContent(
  modifier: Modifier,
  historyViewModel: HistoryViewModel,
) {
  val history by historyViewModel.allHistory.collectAsState()

  if (history.isEmpty()) {
    NoHistoryScreen(modifier)
  } else {
    HistoryListScreen(modifier, history, historyViewModel)
  }
}

@Composable
private fun NoHistoryScreen(modifier: Modifier) {
  Box(modifier = modifier) {
    Image(
      modifier = Modifier
        .wrapContentSize()
        .align(Alignment.Center),
      painter = painterResource(id = R.drawable.il_no_data),
      contentDescription = null,
    )
  }
}

@Composable
private fun HistoryListScreen(
  modifier: Modifier,
  history: List<History>,
  historyViewModel: HistoryViewModel
) {
  val locale by historyViewModel.locale.collectAsState()
  var showClearDialog by rememberSaveable { mutableStateOf(false) }

  @Composable
  fun HistoryListElement(history: History, locale: String) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = FabPadding, vertical = HalfPadding),
      shape = Shapes.large,
      backgroundColor = MaterialTheme.colors.surface,
    ) {
      ConstraintLayout(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
      ) {
        val (labelFrom, textFrom, labelTo, textTo, textDate, imageRemove) = createRefs()

        val guide = createGuidelineFromStart(20f)

        TextMessage(
          modifier = Modifier
            .constrainAs(labelFrom) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(parent.start)
              top.linkTo(parent.top)
              end.linkTo(guide)
            }
            .padding(HalfPadding),
          text = stringResource(id = R.string.label_from),
          textAlign = TextAlign.Start,
        )

        TextMessage(
          modifier = Modifier
            .constrainAs(textFrom) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(guide)
              top.linkTo(parent.top)
              end.linkTo(parent.end)
            }
            .padding(HalfPadding),
          text = history.fromStop.getCurrentName(locale),
          color = MaterialTheme.colors.onPrimary,
        )

        TextMessage(
          modifier = Modifier
            .constrainAs(labelTo) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(parent.start)
              top.linkTo(textFrom.bottom)
              end.linkTo(guide)
            }
            .padding(HalfPadding),
          text = stringResource(id = R.string.label_to),
          textAlign = TextAlign.Start,
        )

        TextMessage(
          modifier = Modifier
            .constrainAs(textTo) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(guide)
              top.linkTo(textFrom.bottom)
              end.linkTo(parent.end)
            }
            .padding(HalfPadding),
          text = history.fromStop.getCurrentName(locale),
          color = MaterialTheme.colors.onPrimary,
        )

        TextMessage(
          modifier = Modifier
            .constrainAs(textDate) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(parent.start)
              top.linkTo(textTo.bottom)
              end.linkTo(guide)
              bottom.linkTo(parent.bottom)
            }
            .padding(HalfPadding),
          text = Date(history.timestamp).format(),
          textAlign = TextAlign.Start,
        )

        IconButton(
          modifier = Modifier
            .constrainAs(imageRemove) {
              width = Dimension.wrapContent
              height = Dimension.wrapContent
              top.linkTo(parent.top)
              end.linkTo(parent.end)
            },
          onClick = { showDeleteDialog = true }) {
          Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = null)
        }

        if (showDeleteDialog) {
          A2bAlertDialog(
            title = stringResource(id = R.string.title_history),
            text = stringResource(id = R.string.message_history_dialog_delete),
            onConfirm = { historyViewModel.removeItem(history) }
          ) { showDeleteDialog = false }
        }
      }
    }
  }

  Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      items(history) { history ->
        HistoryListElement(history = history, locale = locale)
      }
    }

    if (showClearDialog) {
      A2bAlertDialog(
        title = stringResource(id = R.string.title_history),
        text = stringResource(id = R.string.message_history_dialog_clear),
        onConfirm = { historyViewModel.clearHistory() }
      ) { showClearDialog = false }
    }
  }
}

