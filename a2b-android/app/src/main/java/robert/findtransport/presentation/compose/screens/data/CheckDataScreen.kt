package robert.findtransport.presentation.compose.screens.data

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import robert.findtransport.R
import robert.findtransport.data.model.enums.DataLoading
import robert.findtransport.data.model.error.DataDownloadExceptions
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.presentation.compose.reusables.composables.RegularButton
import robert.findtransport.presentation.compose.reusables.composables.TextMessage
import robert.findtransport.presentation.compose.reusables.composables.TextTitle

@Composable
fun CheckDataScreen(
  modifier: Modifier = Modifier,
  dataViewModel: DataViewModel = hiltViewModel(),
  onVpnError: () -> Unit,
) {
  val loadingState by dataViewModel.loaded.collectAsState()

  OnLifecycleEvent { event ->
    when (event) {
      Lifecycle.Event.ON_RESUME -> dataViewModel.checkData()
      else -> return@OnLifecycleEvent
    }
  }

  AnimatedVisibility(
    modifier = Modifier
      .fillMaxWidth()
      .padding(HalfPadding),
    visible = loadingState != DataLoading.Loaded,
  ) {
    Card(
      modifier = modifier.fillMaxWidth(),
      elevation = 0.dp,
      shape = Shapes.medium,
      backgroundColor = MaterialTheme.colors.surface,
    ) {

      when (loadingState) {
        DataLoading.NotStarted, DataLoading.Loading -> {
          Column {
            TextTitle(text = stringResource(id = R.string.message_check_download))

            LinearProgressIndicator(
              modifier = Modifier
                .padding(top = HalfPadding)
                .fillMaxWidth(),
              color = Accent,
            )
          }
        }
        is DataLoading.Failed -> {
          val reason = (loadingState as DataLoading.Failed).reason
          if (reason is DataDownloadExceptions) {
            when (reason) {
              is DataDownloadExceptions.NoInternetException -> NoInternetScreen()
              is DataDownloadExceptions.NotDownloadedException -> NotDownloadedScreen {
                dataViewModel.checkData()
              }
              is DataDownloadExceptions.NotEnoughSpaceException -> NotEnoughSpaceScreen()
              is DataDownloadExceptions.VpnException -> onVpnError.invoke()
            }
          }
        }
        DataLoading.Loaded -> return@Card
      }
    }
  }
}

@Composable
fun OnLifecycleEvent(onEvent: (Lifecycle.Event) -> Unit) {
  val eventHandler = rememberUpdatedState(onEvent)
  val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

  DisposableEffect(lifecycleOwner.value) {
    val lifecycle = lifecycleOwner.value.lifecycle
    val observer = LifecycleEventObserver { _, event ->
      eventHandler.value(event)
    }

    lifecycle.addObserver(observer)
    onDispose {
      lifecycle.removeObserver(observer)
    }
  }
}

@Composable
fun NoInternetScreen() {
  val context = LocalContext.current

  Column {
    TextMessage(text = stringResource(id = R.string.error_no_internet))

    RegularButton(
      modifier = Modifier
        .padding(horizontal = HalfPadding)
        .padding(bottom = SmallPadding)
        .align(Alignment.End),
      text = stringResource(id = R.string.label_open_settings),
      onClick = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
    )
  }
}

@Composable
fun NotDownloadedScreen(onRetry: () -> Unit) {
  Column {
    TextMessage(text = stringResource(id = R.string.error_not_downloaded))

    RegularButton(
      modifier = Modifier
        .padding(horizontal = HalfPadding)
        .padding(bottom = SmallPadding)
        .align(Alignment.End),
      text = stringResource(id = R.string.label_retry),
      onClick = onRetry,
    )
  }
}

@Composable
fun NotEnoughSpaceScreen() {
  val context = LocalContext.current

  Column {
    TextMessage(text = stringResource(id = R.string.error_storage))

    RegularButton(
      modifier = Modifier
        .padding(horizontal = HalfPadding)
        .padding(bottom = SmallPadding)
        .align(Alignment.End),
      text = stringResource(id = R.string.label_open_settings),
      onClick = { context.startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)) },
    )
  }
}
