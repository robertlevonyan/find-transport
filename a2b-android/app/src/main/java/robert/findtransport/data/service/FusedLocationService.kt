package robert.findtransport.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.mapzen.android.lost.api.LocationServices
import com.mapzen.android.lost.api.LostApiClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FusedLocationService(private val context: Context) {
  private var fusedLocationClient: Location? = null
  private var lostApiClient: LostApiClient? = null
  
  @Suppress("EXPERIMENTAL_API_USAGE")
  fun subscribeToCurrentLocation() = callbackFlow<Location> {
    lostApiClient = LostApiClient.Builder(context)
        .run {
          addConnectionCallbacks(object : LostApiClient.ConnectionCallbacks {
            @SuppressLint("MissingPermission")
            override fun onConnected() {
              fusedLocationClient = fusedLocationClient
                  ?.run { fusedLocationClient }
                  ?: kotlin.run {
                    if (lostApiClient?.isConnected == true) {
                      LocationServices.FusedLocationApi.getLastLocation(lostApiClient ?: return)
                    } else null
                  }
              
              val lat = (fusedLocationClient?.latitude ?: return)
              val lng = (fusedLocationClient?.longitude ?: return)

              launch {
                channel.send(Location("current_location").apply {
                  latitude = lat
                  longitude = lng
                })
              }
            }
            
            override fun onConnectionSuspended() = Unit
            
          })
          build()
        }
        ?.apply { connect() }
    
    awaitClose { }
  }

  fun disconnect() {
    lostApiClient?.disconnect()
  }
}