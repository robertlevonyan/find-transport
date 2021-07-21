package robert.findtransport.domain.usecase.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionUseCaseImpl(private val context: Context) : PermissionUseCase {
  override fun hasPermission(permission: String): Boolean =
      ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
  
}