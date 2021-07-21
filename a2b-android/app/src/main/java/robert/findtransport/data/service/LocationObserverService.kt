package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig

class LocationObserverService(private val context: Context) {
  @SuppressLint("MissingPermission")
  @Suppress("EXPERIMENTAL_API_USAGE")
  fun getLocationUpdates() = callbackFlow<Location> {
    val navigation = MapboxNavigation(context, BuildConfig.MAPBOX_TOKEN)

    val locationEngineRequest = LocationEngineRequest.Builder(100)
      .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
      .setMaxWaitTime(10)
      .build()


    val customLocationEngineCallback = object : LocationEngineCallback<LocationEngineResult> {
      override fun onSuccess(result: LocationEngineResult?) {
        launch {
          result?.lastLocation?.let { send(it) }
        }
      }

      override fun onFailure(exception: Exception) {
        error(exception.message ?: "No last location")
      }

    }

    navigation.locationEngine.requestLocationUpdates(
      locationEngineRequest,
      customLocationEngineCallback,
      Looper.getMainLooper()
    )

    awaitClose {
      navigation.locationEngine.removeLocationUpdates(customLocationEngineCallback)
    }
  }
}
