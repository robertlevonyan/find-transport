package robert.findtransport.presentation.screens.stops

import android.location.Address
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import robert.findtransport.base.BaseViewModel
import robert.findtransport.data.model.Stop
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import javax.inject.Inject

@HiltViewModel
class StopsPickerViewModel @Inject constructor(
    localeUseCase: LocaleUseCase,
    private val stopsUseCase: StopsUseCase,
) : BaseViewModel() {
    val locale = MutableStateFlow(localeUseCase.getCurrentLanguage()).asStateFlow()
    val allStops: MutableStateFlow<List<Stop>> = MutableStateFlow(emptyList())

    fun findStops(word: String) {
        println("Find $word")
        viewModelScope.launch(Dispatchers.IO) {
            allStops.value = stopsUseCase.getStops(word, locale.value)
        }
    }

    suspend fun getAddress(stop: Stop): Address? {
        return stopsUseCase.getAddress(stop)
    }
}
