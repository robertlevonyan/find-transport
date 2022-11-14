package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FusedLocationService(private val context: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  @SuppressLint("MissingPermission")
  suspend fun subscribeToCurrentLocation() = suspendCoroutine<Location> { locationContinuation ->
    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        val lastLocation = locationResult.lastLocation

        Location("current_location").also { currentLocation ->
          currentLocation.latitude = lastLocation?.latitude ?: DEFAULT_LATITUDE
          currentLocation.longitude = lastLocation?.longitude ?: DEFAULT_LONGITUDE
        }.let(locationContinuation::resume)

        fusedLocationClient.removeLocationUpdates(this)
      }
    }

    fusedLocationClient.requestLocationUpdates(
      LocationRequest.create(),
      locationCallback,
      context.mainLooper,
    )
  }
}
