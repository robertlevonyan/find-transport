package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationObserverService(private val context: Context) {
  @SuppressLint("MissingPermission")
  @Suppress("EXPERIMENTAL_API_USAGE")
  fun getLocationUpdates() = callbackFlow {
    val locationEngineCallback = object : LocationEngineCallback<LocationEngineResult> {
      override fun onSuccess(locationEngineResult: LocationEngineResult?) {
        if (locationEngineResult == null) return

        locationEngineResult.lastLocation?.let {
          launch { send(it) }
        }
      }

      override fun onFailure(error: Exception) {
        Log.e("Location", "error", error)
      }
    }

    val locationEngine = LocationEngineProvider.getBestLocationEngine(context)
    val locationEngineRequest = LocationEngineRequest.Builder(100)
      .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
      .setMaxWaitTime(10)
      .build()

    locationEngine.requestLocationUpdates(locationEngineRequest, locationEngineCallback, context.mainLooper)
    locationEngine.getLastLocation(locationEngineCallback)

    awaitClose {
      locationEngine.removeLocationUpdates(locationEngineCallback)
    }
  }
}
