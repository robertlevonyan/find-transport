package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationObserverService(private val context: Context) {

    @SuppressLint("MissingPermission")
    @Suppress("EXPERIMENTAL_API_USAGE")
    fun getLocationUpdates() = callbackFlow {
        val locationManager = context.getSystemService(LocationManager::class.java)
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val provider = when {
            gpsEnabled -> LocationManager.GPS_PROVIDER
            networkEnabled -> LocationManager.NETWORK_PROVIDER
            else -> {
                send(null)
                return@callbackFlow
            }
        }

        val onLocationListener = LocationListener { location -> launch { send(location) } }
        locationManager.requestLocationUpdates(provider, 100L, 1f, onLocationListener)

        awaitClose {
            locationManager.removeUpdates(onLocationListener)
        }
    }
}
