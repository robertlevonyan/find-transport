package robert.findtransport.presentation.stop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.presentation.component.ld.SingleLiveEvent

class StopsPickerViewModel(
    private val stopsUseCase: StopsUseCase,
    private val localeUseCase: LocaleUseCase
) : BaseViewModel() {

  val allStops: LiveData<PagingData<Stop>> = stopsUseCase.getStopsPaged().cachedIn(viewModelScope + Dispatchers.IO).asLiveData()

  private val _autocompleteStops = SingleLiveEvent<List<Stop>>()
  val autocompleteStops: LiveData<List<Stop>> get() = _autocompleteStops

  private val _selectedStop = MutableLiveData<Stop>()
  val selectedStop: LiveData<Stop> get() = _selectedStop

  private val _searchMode = MutableLiveData<Boolean>()
  val searchMode: LiveData<Boolean> get() = _searchMode

  private val _showNoData = MutableLiveData<Boolean>()
  val showNoData: LiveData<Boolean> get() = _showNoData

  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private var isInSearchMode = false

  init {
    _searchMode.postValue(isInSearchMode)
    _showNoData.postValue(false)
    _locale.postValue(localeUseCase.getCurrentLanguage())
  }

  fun findStops(word: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val locale = localeUseCase.getCurrentLanguage()
      val stops = stopsUseCase.getStopsAutocomplete(word, locale)
      withContext(Dispatchers.Main) {
        if (stops.isEmpty() && word.isEmpty()) {
//          allStops = stopsUseCase.getStopsPaged().toLiveData(
//              Config(pageSize = 30, enablePlaceholders = false), fetchExecutor = Dispatchers.Default.asExecutor())
          _showNoData.postValue(false)
        } else if (stops.isEmpty() && word.isNotEmpty()) {
          _autocompleteStops.postValue(stops)
          _showNoData.postValue(true)
        } else {
          _autocompleteStops.postValue(stops)
          _showNoData.postValue(false)
        }
      }
    }
  }

  fun onStopClicked(stop: Stop) {
    _selectedStop.postValue(stop)
  }

  fun toggleSearchMode() {
    isInSearchMode = !isInSearchMode
    _searchMode.postValue(isInSearchMode)
  }
}
