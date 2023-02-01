package robert.findtransport.data.service

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddressProviderService @Inject constructor(private val context: Context) {

  suspend fun getAddress(location: Location): Address? = suspendCoroutine { locationContinuation ->
    val geocoder = Geocoder(context, Locale.getDefault())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      geocoder.getFromLocation(
        location.latitude,
        location.longitude,
        1,
      ) { addresses ->
        val address = addresses.firstOrNull()

        locationContinuation.resume(address)
      }
    } else {
      val address = geocoder.getFromLocation(
        location.latitude,
        location.longitude,
        1
      )?.firstOrNull()

      locationContinuation.resume(address)
    }
  }
}