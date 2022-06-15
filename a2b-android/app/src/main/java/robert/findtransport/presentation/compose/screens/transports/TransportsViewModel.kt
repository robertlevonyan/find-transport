package robert.findtransport.presentation.compose.screens.transports

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import javax.inject.Inject

@HiltViewModel
class TransportsViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  private val transportUseCase: TransportUseCase,
) : BaseViewModel() {
  private val _allTransports = MutableSharedFlow<PagingData<Transport>>()
  val allTransports get() = _allTransports.asSharedFlow()

  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()

  fun getTransports(checked: Boolean) {
    viewModelScope.launch {
      transportUseCase.getTransportsPaged(checked)
        .cachedIn(this + Dispatchers.IO)
        .collectLatest(_allTransports::emit)
    }
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
    }
  }

  fun setShowFavoritesToggle(show: Boolean) {
    transportUseCase.showOnlyFavorites = show
  }
}
