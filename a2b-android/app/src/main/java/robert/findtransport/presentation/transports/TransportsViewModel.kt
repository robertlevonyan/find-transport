package robert.findtransport.presentation.transports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.presentation.component.ld.SingleLiveEvent

class TransportsViewModel(
    localeUseCase: LocaleUseCase,
    private val transportUseCase: TransportUseCase,
) : BaseViewModel() {
  private val _allTransports = SingleLiveEvent<List<Transport>>()
  val allTransports: LiveData<List<Transport>> get() = _allTransports

  private val _locale = MutableLiveData<String>()
  val locale: LiveData<String> get() = _locale

  private val _selectedTransport = SingleLiveEvent<Transport>()
  val selectedTransport: LiveData<Transport> get() = _selectedTransport

  private val _showOnlyFavorites = SingleLiveEvent<Boolean>()
  val showOnlyFavorites: LiveData<Boolean> get() = _showOnlyFavorites

  init {
    _locale.postValue(localeUseCase.getCurrentLanguage())
    _showOnlyFavorites.postValue(transportUseCase.showOnlyFavorites)
    getTransports()
  }

  fun getTransports(checked: Boolean = transportUseCase.showOnlyFavorites) {
    viewModelScope.launch(Dispatchers.IO) {
      val transports = transportUseCase.getTransportsPaged(checked)

        _allTransports.postValue(transports)
    }
  }

  fun selectTransport(transport: Transport) {
    _selectedTransport.postValue(transport)
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
