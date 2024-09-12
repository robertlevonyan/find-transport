package robert.findtransport.presentation.screens.transports

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    val buses = MutableStateFlow(emptyList<Transport>())
    val microbuses = MutableStateFlow(emptyList<Transport>())
    val trolleybuses = MutableStateFlow(emptyList<Transport>())
    val metro = MutableStateFlow(emptyList<Transport>())

    init {
        viewModelScope.launch {
            buses.value = transportUseCase.getBuses()
            microbuses.value = transportUseCase.getMicrobuses()
            trolleybuses.value = transportUseCase.getTrolleybuses()
            metro.value = transportUseCase.getMetro()
        }
    }

    override fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            transportUseCase.toggleFavorite(transport)
        }
    }
}
