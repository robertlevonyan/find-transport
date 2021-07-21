package robert.findtransport.presentation.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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

  private val _onComplete by lazy { MutableLiveData<Unit>() }
  val onComplete: LiveData<Unit> get() = _onComplete

  init {
    viewModelScope.launch(Dispatchers.IO) {
      databaseUseCase.clearDb()
      stopsUseCase.downloadStops()
      stopsUseCase.downloadLocations()
      transportUseCase.downloadTransports()
      transportUseCase.downloadJoins()

      _onComplete.postValue(Unit)
    }
  }

}