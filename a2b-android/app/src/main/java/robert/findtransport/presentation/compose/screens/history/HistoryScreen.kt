package robert.findtransport.presentation.compose.screens.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
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
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.FabPadding
import robert.findtransport.presentation.compose.reusables.HalfPadding
import robert.findtransport.presentation.compose.reusables.Shapes
import robert.findtransport.presentation.compose.reusables.composables.A2bAlertDialog
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.TextSecondary
import robert.findtransport.presentation.compose.screens.search.SearchOpenInitiator
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
      navController = navController,
      historyViewModel = historyViewModel,
    )
  }
}

@Composable
private fun HistoryContent(
  modifier: Modifier,
  navController: NavController,
  historyViewModel: HistoryViewModel,
) {
  val history by historyViewModel.allHistory.collectAsState()

  if (history.isEmpty()) {
    NoHistoryScreen(modifier)
  } else {
    HistoryListScreen(modifier, history, navController, historyViewModel)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryListScreen(
  modifier: Modifier,
  history: List<History>,
  navController: NavController,
  historyViewModel: HistoryViewModel
) {
  val locale by historyViewModel.locale.collectAsState()
  var showClearDialog by rememberSaveable { mutableStateOf(false) }

  @Composable
  fun LazyItemScope.HistoryListElement(history: History, locale: String) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreDialog by rememberSaveable { mutableStateOf(false) }

    Card(
      modifier = Modifier
        .animateItemPlacement()
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = FabPadding, vertical = HalfPadding),
      shape = Shapes.medium,
      backgroundColor = MaterialTheme.colors.surface,
    ) {
      ConstraintLayout(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .clickable {
            showRestoreDialog = !showRestoreDialog
          }
      ) {
        val (labelFrom, textFrom, labelTo, textTo, textDate, imageRemove) = createRefs()

        val guide = createGuidelineFromStart(0.35f)

        TextSecondary(
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

        TextSecondary(
          modifier = Modifier
            .constrainAs(textFrom) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(guide)
              top.linkTo(parent.top)
              end.linkTo(imageRemove.start)
            }
            .padding(HalfPadding),
          text = history.fromStop.getCurrentName(locale),
          color = MaterialTheme.colors.onPrimary,
          textAlign = TextAlign.Start,
        )

        TextSecondary(
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

        TextSecondary(
          modifier = Modifier
            .constrainAs(textTo) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(guide)
              top.linkTo(textFrom.bottom)
              end.linkTo(imageRemove.start)
            }
            .padding(HalfPadding),
          text = history.toStop.getCurrentName(locale),
          color = MaterialTheme.colors.onPrimary,
          textAlign = TextAlign.Start,
        )

        TextSecondary(
          modifier = Modifier
            .constrainAs(textDate) {
              width = Dimension.fillToConstraints
              height = Dimension.wrapContent
              start.linkTo(parent.start)
              top.linkTo(textTo.bottom)
              end.linkTo(parent.end)
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
            onConfirm = {
              showDeleteDialog = false
              historyViewModel.removeItem(history)
            },
            onDismiss = { showDeleteDialog = false },
          ) { showDeleteDialog = false }
        }

        if (showRestoreDialog) {
          A2bAlertDialog(
            title = stringResource(id = R.string.title_history),
            text = stringResource(id = R.string.message_history_dialog_restore),
            onConfirm = {
              showRestoreDialog = false
              navController.navigate(
                route = NavigationScreens.SearchScreen.name +
                    "?from_id=${history.fromStop.id}&to_id=${history.toStop.id}" +
                    "&opened=${SearchOpenInitiator.HISTORY.name}"
              )
            },
            onDismiss = { showRestoreDialog = false },
          ) { showRestoreDialog = false }
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

    FloatingActionButton(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(FabPadding),
      onClick = { showClearDialog = !showClearDialog }
    ) {
      Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = null)
    }

    if (showClearDialog) {
      A2bAlertDialog(
        title = stringResource(id = R.string.title_history),
        text = stringResource(id = R.string.message_history_dialog_clear),
        onConfirm = {
          historyViewModel.clearHistory()
          showClearDialog = false
        },
        onDismiss = { showClearDialog = false },
      ) { showClearDialog = false }
    }
  }
}
