package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FusedLocationService(private val context: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  @SuppressLint("MissingPermission")
  @Suppress("EXPERIMENTAL_API_USAGE")
  fun subscribeToCurrentLocation() = callbackFlow<Location> {
    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        val lastLocation = locationResult.lastLocation

        launch {
          channel.send(Location("current_location").also { currentLocation ->
            currentLocation.latitude = lastLocation.latitude
            currentLocation.longitude = lastLocation.longitude
          })
        }
      }

      override fun onLocationAvailability(p0: LocationAvailability) {
        super.onLocationAvailability(p0)
      }
    }

    fusedLocationClient.requestLocationUpdates(
      LocationRequest.create(),
      locationCallback,
      context.mainLooper,
    )

    awaitClose {
      fusedLocationClient.removeLocationUpdates(locationCallback)
    }
  }
}
