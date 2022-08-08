package robert.findtransport.base

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import robert.findtransport.BuildConfig
import robert.findtransport.data.model.DataLoading
import robert.findtransport.data.model.Result
import robert.findtransport.data.service.SharedPreferencesService
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.feedback.FeedbackUseCase
import robert.findtransport.domain.usecase.network.CheckInternetUseCase
import robert.findtransport.domain.usecase.preference.IntroUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.domain.usecase.preference.VersionUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.PREF_LANGUAGE
import robert.findtransport.utils.PREF_THEME
import java.io.EOFException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val checkInternetUseCase: CheckInternetUseCase,
  themeUseCase: ThemeUseCase,
  localeUseCase: LocaleUseCase,
  private val versionUseCase: VersionUseCase,
  private val introUseCase: IntroUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val databaseUseCase: DatabaseUseCase,
  private val feedbackUseCase: FeedbackUseCase,
) : BaseViewModel() {
  val theme = MutableStateFlow(themeUseCase.getTheme())
  val currentLanguage = MutableStateFlow(localeUseCase.getCurrentLanguage())

  private val _loaded = MutableStateFlow<DataLoading>(DataLoading.NotStarted)

  private val _emptyDatabase = MutableSharedFlow<Unit>()
  val emptyDatabase: Flow<Unit> get() = _emptyDatabase

  private val _nextIntro = MutableSharedFlow<Unit>()
  val nextIntro: Flow<Unit> get() = _nextIntro

  private val _nextMain = MutableSharedFlow<Unit>()
  val nextMain: Flow<Unit> get() = _nextMain

  private val _loadingError = MutableSharedFlow<String>()
  val loadingError: Flow<String> get() = _loadingError

  private val _loadingDiskFull = MutableSharedFlow<Unit>()
  val loadingDiskFull: Flow<Unit> get() = _loadingDiskFull

  private var downloaded = false

  init {
    viewModelScope.launch {
      launch {
        SharedPreferencesService.getPreferenceChangedValue<Int>(PREF_THEME).collectLatest { value ->
          theme.value = value
        }
      }
      launch {
        SharedPreferencesService.getPreferenceChangedValue<String>(PREF_LANGUAGE).collectLatest { value ->
          currentLanguage.value = value
        }
      }
    }
    checkData()
  }

  private fun checkData() {
    if (_loaded.value == DataLoading.Loaded || _loaded.value == DataLoading.Loading) {
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      if (checkInternetUseCase.isVpnConnected()) {
        notifyLoadingError("No Internet")
        return@launch
      }
      if (checkInternetUseCase.isResolveIp() && checkInternetUseCase.isInternetConnected()) {
        _loaded.value = DataLoading.Loading
        try {
          if (!versionUseCase.isNewerVersion() && !databaseUseCase.isDatabaseEmpty()) {
            notifyLoaded()
          } else {
            if (!databaseUseCase.isDatabaseEmpty()) {
              databaseUseCase.clearDb()
            }
            getTransports()
            getStops()
            if (downloaded) {
              notifyLoaded()
            } else {
              notifyLoadingError("Not downloaded")
            }
          }
        } catch (e: Exception) {
          val message = e.message ?: ""
          if (message.contains("database or disk is full (code 13)")) {
            _loadingDiskFull.emit(Unit)
          }
          notifyLoadingError(message)
        }
      } else {
        if (databaseUseCase.isDatabaseEmpty()) {
          notifyEmptyDatabase()
        } else {
          notifyLoaded()
        }
      }
    }
  }

  private fun notifyLoaded() {
    viewModelScope.launch {
      onNext()
      _loaded.value = DataLoading.Loaded
    }
  }

  private fun notifyEmptyDatabase() {
    viewModelScope.launch {
      _loaded.value = DataLoading.Failed(Exception())
      _emptyDatabase.emit(Unit)
    }
  }

  private fun notifyLoadingError(message: String?) {
    viewModelScope.launch(Dispatchers.IO) {
      _loaded.value = DataLoading.Failed(Exception(message))
      _loadingError.emit(message ?: return@launch)
      feedbackUseCase.sendFeedback("error@a2b.com", "Splash", message)
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

  private suspend fun onNext() {
    if (introUseCase.isIntroPassed) {
      _nextMain.emit(Unit)
    } else {
      _nextIntro.emit(Unit)
    }
  }

  fun sendErrorFeedback(thread: Thread, throwable: Throwable) {
    viewModelScope.launch(Dispatchers.IO) {
      feedbackUseCase.sendFeedback(
        email = "error@a2b.com",
        subject = "ActivityThread",
        message = """
            Thread name: ${thread.name}
            Version: ${BuildConfig.VERSION_CODE}
            Error message: ${throwable.message} 
            Stacktrace: ${throwable.stackTrace.joinToString("\n")}
          """.trimIndent(),
      )
    }
  }
}