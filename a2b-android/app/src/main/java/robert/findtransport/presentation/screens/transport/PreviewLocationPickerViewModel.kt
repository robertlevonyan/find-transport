package robert.findtransport.presentation.screens.transport

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.presentation.screens.map.LocationPickerViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewLocationPickerViewModel @Inject constructor(
  localeUseCase: LocaleUseCase,
  permissionUseCase: PermissionUseCase,
  locationUseCase: LocationUseCase,
) : LocationPickerViewModel(localeUseCase, permissionUseCase, locationUseCase) {
  val isPrimary = MutableStateFlow(true)
}
