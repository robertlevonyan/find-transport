package robert.findtransport.presentation.compose.screens.history

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.History
import robert.findtransport.domain.usecase.history.HistoryUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
  private val localeUseCase: LocaleUseCase,
  private val historyUseCase: HistoryUseCase
) : BaseViewModel() {

  private val _loading = MutableStateFlow(true)
  val loading: Flow<Boolean> get() = _loading

  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: Flow<String> get() = _locale

  private val _noHistory = MutableSharedFlow<Boolean>()
  val noHistory: Flow<Boolean> get() = _noHistory

  private val _allHistory = MutableSharedFlow<List<History>>()
  val allHistory: Flow<List<History>> get() = _allHistory

  private val _itemRemoved = MutableSharedFlow<History>()
  val itemRemoved: Flow<History> get() = _itemRemoved

  private val _historyCleared = MutableSharedFlow<Unit>()
  val historyCleared: Flow<Unit> get() = _historyCleared

  fun loadHistory() {
    viewModelScope.launch(Dispatchers.IO) {
      val history = historyUseCase.getHistory()
      _allHistory.emit(history)
      _noHistory.emit(history.isEmpty())
      _loading.value = false
    }
  }

  fun clearHistory() {
    viewModelScope.launch(Dispatchers.IO) {
      historyUseCase.clearHistory()
      _historyCleared.emit(Unit)
    }
  }

  fun removeItem(history: History) {
    viewModelScope.launch(Dispatchers.IO) {
      historyUseCase.removeHistoryItem(history.id)
      _itemRemoved.emit(history)
    }
  }

  fun setNoHistory() {
    viewModelScope.launch {
      _noHistory.emit(true)
    }
  }
}
