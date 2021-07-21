package robert.findtransport.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Result
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.feedback.FeedbackUseCase
import robert.findtransport.domain.usecase.network.CheckInternetUseCase
import robert.findtransport.domain.usecase.preference.IntroUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import robert.findtransport.domain.usecase.preference.VersionUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import java.io.EOFException

class SplashViewModel(
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
  private val _themeLiveData by lazy { MutableLiveData<Int>() }
  val themeLiveData: LiveData<Int> get() = _themeLiveData

  private val _currentLanguage by lazy { MutableLiveData<String>() }
  val currentLanguage: LiveData<String> get() = _currentLanguage

  private val _loadStart = MutableLiveData<Unit>()
  val loadStart: LiveData<Unit> get() = _loadStart

  private val _loaded = MutableLiveData<Unit>()
  val loaded: LiveData<Unit> get() = _loaded

  private val _emptyDatabase = MutableLiveData<Unit>()
  val emptyDatabase: LiveData<Unit> get() = _emptyDatabase

  private val _nextIntro = MutableLiveData<Unit>()
  val nextIntro: LiveData<Unit> get() = _nextIntro

  private val _nextMain = MutableLiveData<Unit>()
  val nextMain: LiveData<Unit> get() = _nextMain

  private val _loadingError = MutableLiveData<String>()
  val loadingError: LiveData<String> get() = _loadingError

  private val _loadingDiskFull = MutableLiveData<Unit>()
  val loadingDiskFull: LiveData<Unit> get() = _loadingDiskFull

  private var downloaded = false

  init {
    _themeLiveData.postValue(themeUseCase.getTheme())
    _currentLanguage.postValue(localeUseCase.getCurrentLanguage())
    checkData()
  }

  fun checkData() {
    viewModelScope.launch(Dispatchers.IO) {
      if (checkInternetUseCase.isVpnConnected()) {
        notifyLoadingError("No Internet")
        return@launch
      }
      if (checkInternetUseCase.isResolveIp() && checkInternetUseCase.isInternetConnected()) {
        _loadStart.postValue(Unit)
        try {
          if (!versionUseCase.isNewerVersion() && !databaseUseCase.isDatabaseEmpty()) {
            notifyLoaded()
          } else {
            if (databaseUseCase.isDatabaseEmpty()) {
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
            _loadingDiskFull.postValue(Unit)
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
    _loaded.postValue(Unit)
  }

  private fun notifyEmptyDatabase() {
    _emptyDatabase.postValue(Unit)
  }

  private fun notifyLoadingError(message: String?) {
    _loadingError.postValue(message ?: return)
    viewModelScope.launch(Dispatchers.IO) {
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

  fun onNext() = if (introUseCase.isIntroPassed) {
    _nextMain.postValue(Unit)
  } else {
    _nextIntro.postValue(Unit)
  }
}