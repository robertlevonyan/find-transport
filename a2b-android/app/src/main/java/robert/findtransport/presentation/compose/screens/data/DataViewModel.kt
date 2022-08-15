package robert.findtransport.presentation.compose.screens.data

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.DataLoading
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.error.DataDownloadExceptions
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.network.CheckInternetUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.domain.usecase.preference.VersionUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import java.io.EOFException
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
  themeUseCase: ThemeUseCase,
  localeUseCase: LocaleUseCase,
  private val checkInternetUseCase: CheckInternetUseCase,
  private val versionUseCase: VersionUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val databaseUseCase: DatabaseUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val theme = MutableStateFlow(themeUseCase.getTheme()).asStateFlow()

  private val loadedFlow = MutableStateFlow<DataLoading>(DataLoading.NotStarted)
  val loaded get() = loadedFlow.asStateFlow()

  private var downloaded = false

  fun checkData() {
    if (loadedFlow.value == DataLoading.Loaded || loadedFlow.value == DataLoading.Loading) {
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      if (checkInternetUseCase.isVpnConnected()) {
        loadedFlow.value = DataLoading.Failed(DataDownloadExceptions.VpnException())
        return@launch
      }
      if (!checkInternetUseCase.isResolveIp() || !checkInternetUseCase.isInternetConnected()) {
        if (databaseUseCase.isDatabaseEmpty()) {
          loadedFlow.value = DataLoading.Failed(DataDownloadExceptions.NoInternetException())
          return@launch
        } else {
          loadedFlow.value = DataLoading.Loaded
        }
      }

      downloadData()
    }
  }

  private fun downloadData() {
    viewModelScope.launch {
      loadedFlow.value = DataLoading.Loading
      try {
        if (!versionUseCase.isNewerVersion() && !databaseUseCase.isDatabaseEmpty()) {
          loadedFlow.value = DataLoading.Loaded
        } else {
          if (!databaseUseCase.isDatabaseEmpty()) {
            databaseUseCase.clearDb()
          }
          getTransports()
          getStops()
          if (downloaded) {
            loadedFlow.value = DataLoading.Loaded
          } else {
            loadedFlow.value = DataLoading.Failed(DataDownloadExceptions.NotDownloadedException())
          }
        }
      } catch (e: Exception) {
        val message = e.message ?: ""
        loadedFlow.value = DataLoading.Failed(
          if (message.contains("database or disk is full (code 13)")) {
            DataDownloadExceptions.NotEnoughSpaceException()
          } else {
            e
          }
        )
      }
    }
  }

  private suspend fun getStops() = withContext(Dispatchers.IO) {
    when (val result = stopsUseCase.downloadStops()) {
      is Result.Error -> if (result.exception.error is EOFException) {
        downloaded = false
      }
      else -> downloaded = true
    }
    when (val result = stopsUseCase.downloadLocations()) {
      is Result.Error -> if (result.exception.error is EOFException) {
        downloaded = false
      }
      else -> downloaded = true
    }
  }

  private suspend fun getTransports() = withContext(Dispatchers.IO) {
    when (val result = transportUseCase.downloadTransports()) {
      is Result.Error -> if (result.exception.error is EOFException) {
        downloaded = false
      }
      else -> downloaded = true
    }
    when (val result = transportUseCase.downloadJoins()) {
      is Result.Error -> if (result.exception.error is EOFException) {
        downloaded = false
      }
      else -> downloaded = true
    }
  }
}