package robert.findtransport.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.History
import robert.findtransport.domain.usecase.history.HistoryUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.presentation.component.ld.SingleLiveEvent

class HistoryViewModel(
  localeUseCase: LocaleUseCase,
  private val historyUseCase: HistoryUseCase
) : BaseViewModel() {

  private val _loading = SingleLiveEvent<Boolean>()
  val loading: LiveData<Boolean> get() = _loading

  private val _locale = SingleLiveEvent<String>()
  val locale: LiveData<String> get() = _locale

  private val _noHistory = SingleLiveEvent<Boolean>()
  val noHistory: LiveData<Boolean> get() = _noHistory

  private val _allHistory = SingleLiveEvent<List<History>>()
  val allHistory: LiveData<List<History>> get() = _allHistory

  private val _onClear = SingleLiveEvent<Unit>()
  val onClear: LiveData<Unit> get() = _onClear

  private val _itemClear = SingleLiveEvent<History>()
  val itemClear: LiveData<History> get() = _itemClear

  private val _itemClicked = SingleLiveEvent<History>()
  val itemClicked: LiveData<History> get() = _itemClicked

  private val _itemRemoved = SingleLiveEvent<History>()
  val itemRemoved: LiveData<History> get() = _itemRemoved

  private val _historyCleared = SingleLiveEvent<Unit>()
  val historyCleared: LiveData<Unit> get() = _historyCleared

  init {
    _loading.postValue(true)
    _locale.postValue(localeUseCase.getCurrentLanguage())
    viewModelScope.launch(Dispatchers.IO) {
      val history = historyUseCase.getHistory()
      _allHistory.postValue(history)
      _noHistory.postValue(history.isEmpty())
      _loading.postValue(false)
    }
  }

  fun onClearClicked() {
    _onClear.postValue(Unit)
  }

  fun onRemoveItemClicked(history: History) {
    _itemClear.postValue(history)
  }

  fun onItemClicked(history: History) {
    _itemClicked.postValue(history)
  }

  fun clearHistory() {
    viewModelScope.launch(Dispatchers.IO) {
      historyUseCase.clearHistory()
      _historyCleared.postValue(Unit)
    }
  }

  fun removeItem(history: History) {
    viewModelScope.launch(Dispatchers.IO) {
      historyUseCase.removeHistoryItem(history.id)
      _itemRemoved.postValue(history)
    }
  }

  fun setNoHistory() {
    _noHistory.postValue(true)
  }
}
