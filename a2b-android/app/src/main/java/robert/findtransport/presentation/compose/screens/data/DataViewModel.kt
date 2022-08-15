package robert.findtransport.presentation.compose.screens.data

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
  private val _theme = MutableStateFlow(themeUseCase.getTheme())
  val theme: Flow<Int> get() = _theme

  private val _currentLanguage = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val currentLanguage: Flow<String> get() = _currentLanguage

  private val _loaded = MutableStateFlow<DataLoading>(DataLoading.NotStarted)
  val loaded: StateFlow<DataLoading> get() = _loaded

  private var downloaded = false

  fun checkData() {
    if (_loaded.value == DataLoading.Loaded || _loaded.value == DataLoading.Loading) {
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      if (checkInternetUseCase.isVpnConnected()) {
        _loaded.value = DataLoading.Failed(DataDownloadExceptions.VpnException())
        return@launch
      }
      if (!checkInternetUseCase.isResolveIp() || !checkInternetUseCase.isInternetConnected()) {
        if (databaseUseCase.isDatabaseEmpty()) {
          _loaded.value = DataLoading.Failed(DataDownloadExceptions.NoInternetException())
          return@launch
        } else {
          _loaded.value = DataLoading.Loaded
        }
      }

      downloadData()
    }
  }

  private fun downloadData() {
    viewModelScope.launch {
      _loaded.value = DataLoading.Loading
      try {
        if (!versionUseCase.isNewerVersion() && !databaseUseCase.isDatabaseEmpty()) {
          _loaded.value = DataLoading.Loaded
        } else {
          if (!databaseUseCase.isDatabaseEmpty()) {
            databaseUseCase.clearDb()
          }
          getTransports()
          getStops()
          if (downloaded) {
            _loaded.value = DataLoading.Loaded
          } else {
            _loaded.value = DataLoading.Failed(DataDownloadExceptions.NotDownloadedException())
          }
        }
      } catch (e: Exception) {
        val message = e.message ?: ""
        _loaded.value = DataLoading.Failed(
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