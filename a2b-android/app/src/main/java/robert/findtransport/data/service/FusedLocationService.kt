package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE

class FusedLocationService(private val context: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  @SuppressLint("MissingPermission")
  @Suppress("EXPERIMENTAL_API_USAGE")
  fun subscribeToCurrentLocation() = callbackFlow {
    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        val lastLocation = locationResult.lastLocation

        launch {
          channel.send(Location("current_location").also { currentLocation ->
            currentLocation.latitude = lastLocation?.latitude ?: DEFAULT_LATITUDE
            currentLocation.longitude = lastLocation?.longitude ?: DEFAULT_LONGITUDE
          })
        }
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
