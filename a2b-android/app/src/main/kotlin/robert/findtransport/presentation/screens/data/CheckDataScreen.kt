package robert.findtransport.presentation.screens.data

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import robert.findtransport.R
import robert.findtransport.data.model.enums.DataLoading
import robert.findtransport.data.model.error.DataDownloadExceptions
import robert.findtransport.presentation.reusables.theme.Accent
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.Shapes
import robert.findtransport.presentation.reusables.theme.ToolbarSize
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.screens.data.components.NoInternetScreen
import robert.findtransport.presentation.screens.data.components.NotDownloadedScreen
import robert.findtransport.presentation.screens.data.components.NotEnoughSpaceScreen
import robert.findtransport.presentation.screens.data.components.OnLifecycleEvent

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
      .padding(top = ToolbarSize)
      .padding(HalfPadding),
    visible = loadingState != DataLoading.Loaded,
  ) {
    Card(
      modifier = modifier.fillMaxWidth(),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = Shapes.medium,
    ) {
      when (loadingState) {
        DataLoading.NotStarted, DataLoading.Loading -> {
          Column {
            TextPrimary(text = stringResource(id = R.string.message_check_download))

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
                dataViewModel.checkData(isPreviouslyFailed = true)
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
