package robert.findtransport.presentation.compose.screens.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.DataLoading
import robert.findtransport.data.model.error.DataDownloadExceptions
import robert.findtransport.domain.usecase.data.DownloadDataUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.preference.ThemeUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val themeUseCase: ThemeUseCase,
  private val localeUseCase: LocaleUseCase,
  private val dataUseCase: DownloadDataUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val theme = MutableStateFlow(themeUseCase.getTheme()).asStateFlow()

  private val loadedFlow = MutableStateFlow<DataLoading>(DataLoading.NotStarted)
  val loaded: StateFlow<DataLoading> get() = loadedFlow

  fun changeLanguage(language: String) {
    viewModelScope.launch {
      localeUseCase.saveLanguage(language)
    }
  }

  fun changeTheme(theme: Int) {
    viewModelScope.launch {
      themeUseCase.saveTheme(theme)
    }
  }

  fun checkForUpdate() {
    viewModelScope.launch {
      dataUseCase.downloadData()
        .catch { e ->
          loadedFlow.value = when (e) {
            is DataDownloadExceptions.VpnException ->
              DataLoading.Failed(DataDownloadExceptions.VpnException())
            is DataDownloadExceptions.NoInternetException ->
              DataLoading.Failed(DataDownloadExceptions.NoInternetException())
            is DataDownloadExceptions.NotEnoughSpaceException ->
              DataLoading.Failed(DataDownloadExceptions.NotEnoughSpaceException())
            is DataDownloadExceptions.NotDownloadedException ->
              DataLoading.Failed(DataDownloadExceptions.NotDownloadedException())
            else -> return@catch
          }
        }
        .collectLatest {
          delay(2000)
          loadedFlow.value = it
        }
    }
  }
}
