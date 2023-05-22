package robert.findtransport.presentation.screens.search

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.search.NewSearchUseCase
import robert.findtransport.domain.usecase.search.SearchUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
  private val searchUseCase: SearchUseCase,
  private val newSearchUseCase: NewSearchUseCase,
) : BaseViewModel() {
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val fromStop = MutableStateFlow(Stop.EMPTY)
  val toStop = MutableStateFlow(Stop.EMPTY)
  val searchResults = MutableStateFlow<SearchState>(SearchState.NotStarted)

  fun performSearch(
    originName: String,
    originStopId: Int,
    originLatitude: Float,
    originLongitude: Float,
    destinationName: String,
    destinationStopId: Int,
    destinationLatitude: Float,
    destinationLongitude: Float,
    opened: String
  ) {
    viewModelScope.launch {
      fromStop.value = stopsUseCase.getStop(originStopId)
      toStop.value = stopsUseCase.getStop(destinationStopId)

      newSearchUseCase.invoke(
        originName,
        originLatitude,
        originLongitude,
        destinationName,
        destinationLatitude,
        destinationLongitude,
        opened
      ).catch { e ->
        if (e is A2bException) {
          searchResults.value = SearchState.Failed(e.type)
        }
      }
        .collectLatest { searchState -> searchResults.value = searchState }
    }
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
    }
  }
}
