package robert.findtransport.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.enums.DataLoading
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
  val loaded = MutableStateFlow<DataLoading>(DataLoading.NotStarted)

  fun changeLanguage(language: String) {
    viewModelScope.launch { localeUseCase.saveLanguage(language) }
  }

  fun changeTheme(theme: Int) {
    viewModelScope.launch { themeUseCase.saveTheme(theme) }
  }

  fun checkForUpdate() {
    viewModelScope.launch {
      dataUseCase.forceDownloadData()
        .catch { e ->
          loaded.value = when (e) {
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
        .collectLatest { loadingState ->
          loaded.value = loadingState
        }
    }
  }
}
