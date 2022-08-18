package robert.findtransport.presentation.compose.screens.history

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.History
import robert.findtransport.domain.usecase.history.HistoryUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val historyUseCase: HistoryUseCase
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val allHistory = historyUseCase.getHistory().stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = emptyList(),
  )

  fun clearHistory() {
    viewModelScope.launch { historyUseCase.clearHistory() }
  }

  fun removeItem(history: History) {
    viewModelScope.launch { historyUseCase.removeHistoryItem(history.id) }
  }
}
