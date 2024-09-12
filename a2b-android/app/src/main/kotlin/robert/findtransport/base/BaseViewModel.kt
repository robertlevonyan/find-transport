package robert.findtransport.base

import androidx.lifecycle.ViewModel
import robert.findtransport.data.model.Transport

abstract class BaseViewModel : ViewModel() {
    open fun toggleTransportFavorite(transport: Transport, toggleFinishAction: () -> Unit = {}) =
        Unit
}
