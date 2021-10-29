package robert.findtransport.presentation.transports

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import javax.inject.Inject

@HiltViewModel
class TransportsViewModel @Inject constructor(
  private val localeUseCase: LocaleUseCase,
  private val transportUseCase: TransportUseCase,
) : BaseViewModel() {
  private val _allTransports = MutableSharedFlow<List<Transport>>()
  val allTransports: Flow<List<Transport>> get() = _allTransports

  private val _locale = MutableStateFlow(localeUseCase.getCurrentLanguage())
  val locale: Flow<String> get() = _locale

  private val _selectedTransport = MutableSharedFlow<Transport>()
  val selectedTransport: Flow<Transport> get() = _selectedTransport

  private val _showOnlyFavorites = MutableStateFlow(transportUseCase.showOnlyFavorites)
  val showOnlyFavorites: Flow<Boolean> get() = _showOnlyFavorites

  init {
    getTransports()
  }

  fun getTransports(checked: Boolean = transportUseCase.showOnlyFavorites) {
    viewModelScope.launch(Dispatchers.IO) {
      val transports = transportUseCase.getTransportsPaged(checked)

      _allTransports.emit(transports)
    }
  }

  fun selectTransport(transport: Transport) {
    viewModelScope.launch(Dispatchers.IO) {
      _selectedTransport.emit(transport)
    }
  }

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
      getTransports()
    }
  }

  fun setShowFavoritesToggle(show: Boolean) {
    transportUseCase.showOnlyFavorites = show
  }
}
