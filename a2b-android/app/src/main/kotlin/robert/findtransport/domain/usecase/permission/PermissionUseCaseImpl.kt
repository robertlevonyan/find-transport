package robert.findtransport.domain.usecase.permission

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import robert.findtransport.data.service.ApplicationContextProvider
import javax.inject.Inject

class PermissionUseCaseImpl @Inject constructor(
  private val applicationContextProvider: ApplicationContextProvider
) : PermissionUseCase {
  override fun hasPermission(permission: String): Boolean =
    ActivityCompat.checkSelfPermission(applicationContextProvider.getApplicationContext(), permission) ==
        PackageManager.PERMISSION_GRANTED
}
