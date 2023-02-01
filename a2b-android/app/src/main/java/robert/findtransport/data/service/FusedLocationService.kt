package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.CancellableContinuation
import robert.findtransport.utils.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FusedLocationService @Inject constructor(private val context: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  @SuppressLint("MissingPermission")
  suspend fun getCurrentLocationAddress(): Location? = suspendCoroutine { locationContinuation ->
    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        val lastLocation = locationResult.lastLocation

        Location("current_location").also { currentLocation ->
          currentLocation.latitude = lastLocation?.latitude ?: DEFAULT_LATITUDE
          currentLocation.longitude = lastLocation?.longitude ?: DEFAULT_LONGITUDE
        }.let { location ->
          if (locationContinuation is CancellableContinuation && !locationContinuation.isActive) {
            return
          }
          try {
            locationContinuation.resume(location)
          } catch (e: Exception) {
            e.printStackTrace()
          } finally {
            fusedLocationClient.removeLocationUpdates(this)
          }
        }
      }
    }

    fusedLocationClient.requestLocationUpdates(
      LocationRequest.create(),
      locationCallback,
      context.mainLooper,
    )
  }
}
