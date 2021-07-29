package robert.findtransport.presentation.update

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase

class UpdateViewModel(
  private val databaseUseCase: DatabaseUseCase,
  private val stopsUseCase: StopsUseCase,
  private val transportUseCase: TransportUseCase,
) : BaseViewModel() {

  private val _onComplete = MutableSharedFlow<Unit>()
  val onComplete: Flow<Unit> get() = _onComplete

  init {
    viewModelScope.launch(Dispatchers.IO) {
      databaseUseCase.clearDb()
      stopsUseCase.downloadStops()
      stopsUseCase.downloadLocations()
      transportUseCase.downloadTransports()
      transportUseCase.downloadJoins()

      _onComplete.emit(Unit)
    }
  }

}