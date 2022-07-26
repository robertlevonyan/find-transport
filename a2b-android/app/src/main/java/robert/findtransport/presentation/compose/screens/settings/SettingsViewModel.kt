package robert.findtransport.presentation.compose.screens.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.LanguageData
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.ThemeData
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.domain.usecase.settings.SettingsUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val settingsUseCase: SettingsUseCase,
  private val themeUseCase: ThemeUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val databaseUseCase: DatabaseUseCase
) : BaseViewModel() {
  private val _themeSave = MutableSharedFlow<Unit>()
  val themeSave: Flow<Unit> get() = _themeSave

  private val _languageSave = MutableSharedFlow<Unit>()
  val languageSave: Flow<Unit> get() = _languageSave

  private val _checkingVersion = MutableStateFlow<DownloadStatus>(DownloadStatus.NotDownloading)
  val checkingVersion: StateFlow<DownloadStatus> get() = _checkingVersion

  fun changeLanguage(languageData: LanguageData) {
    viewModelScope.launch {
      settingsUseCase.saveLanguage(languageData.languageShortSetting)
      _languageSave.emit(Unit)
    }
  }

  fun changeTheme(themeData: ThemeData) {
    viewModelScope.launch {
      themeUseCase.saveTheme(themeData.theme)
      _themeSave.emit(Unit)
    }
  }

  fun checkForUpdate() {
    viewModelScope.launch {
      _checkingVersion.value = DownloadStatus.DownloadStarted
      withContext(Dispatchers.IO) {
        databaseUseCase.clearDb()
        getData()
        delay(1000)
        _checkingVersion.value = DownloadStatus.DownloadCompleted
      }
    }
  }

  private suspend fun getData() = withContext(Dispatchers.IO) {
    stopsUseCase.downloadStops().let { downloadResult ->
      if (downloadResult is Result.Error) {
        _checkingVersion.value = DownloadStatus.DownloadFailed
        return@withContext
      }
    }
    stopsUseCase.downloadLocations().let { downloadResult ->
      if (downloadResult is Result.Error) {
        _checkingVersion.value = DownloadStatus.DownloadFailed
        return@withContext
      }
    }
    transportUseCase.downloadTransports().let { downloadResult ->
      if (downloadResult is Result.Error) {
        _checkingVersion.value = DownloadStatus.DownloadFailed
        return@withContext
      }
    }
    transportUseCase.downloadJoins().let { downloadResult ->
      if (downloadResult is Result.Error) {
        _checkingVersion.value = DownloadStatus.DownloadFailed
        return@withContext
      }
    }
  }
}

sealed class DownloadStatus {
  object NotDownloading : DownloadStatus()
  object DownloadStarted : DownloadStatus()
  object DownloadCompleted : DownloadStatus()
  object DownloadFailed : DownloadStatus()
}
