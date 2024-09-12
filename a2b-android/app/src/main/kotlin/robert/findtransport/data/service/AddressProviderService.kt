package robert.findtransport.data.service

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddressProviderService @Inject constructor(private val context: Context) {

    suspend fun getAddress(location: Location): Address? =
        suspendCoroutine { locationContinuation ->
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            locationContinuation.resume(addresses.firstOrNull())
                        }

                        override fun onError(errorMessage: String?) {
                            super.onError(errorMessage)
                            locationContinuation.resume(null)
                        }
                    },
                )
            } else {
                try {
                    val address = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )?.firstOrNull()

                    locationContinuation.resume(address)
                } catch (e: Exception) {
                    locationContinuation.resume(null)
                }
            }
        }
}