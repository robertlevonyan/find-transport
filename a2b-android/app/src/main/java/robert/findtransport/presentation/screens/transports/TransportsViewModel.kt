package robert.findtransport.presentation.screens.transports

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
  val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
  val buses = transportUseCase.getBusesPaged()
    .cachedIn(scope = viewModelScope + Dispatchers.IO)
  val microbuses = transportUseCase.getMicrobusesPaged()
    .cachedIn(scope = viewModelScope + Dispatchers.IO)
  val trolleybuses = transportUseCase.getTrolleybusesPaged()
    .cachedIn(scope = viewModelScope + Dispatchers.IO)
  val metro = transportUseCase.getMetroPaged()
    .cachedIn(scope = viewModelScope + Dispatchers.IO)

  override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      transportUseCase.toggleFavorite(transport)
    }
  }
}
