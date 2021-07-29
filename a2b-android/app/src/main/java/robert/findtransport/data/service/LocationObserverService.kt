package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.trip.session.LocationObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import robert.findtransport.BuildConfig

class LocationObserverService(private val context: Context) {
  @SuppressLint("MissingPermission")
  @Suppress("EXPERIMENTAL_API_USAGE")
  fun getLocationUpdates() = callbackFlow<Location> {
    val locationEngineRequest = LocationEngineRequest.Builder(100)
      .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
      .setMaxWaitTime(10)
      .build()

    val navigation = MapboxNavigation(
      NavigationOptions.Builder(context.applicationContext)
        .accessToken(BuildConfig.MAPBOX_TOKEN)
        .locationEngineRequest(locationEngineRequest)
        .build()
    )

    val locationObserver = object : LocationObserver {
      override fun onEnhancedLocationChanged(enhancedLocation: Location, keyPoints: List<Location>) {
        launch { send(enhancedLocation) }
      }

      override fun onRawLocationChanged(rawLocation: Location) = Unit
    }

    navigation.registerLocationObserver(locationObserver)

    awaitClose {
      navigation.unregisterLocationObserver(locationObserver)
    }
  }
}
