package robert.findtransport.data.model

import android.location.Address

data class StopWithAddress(
    val stop: Stop,
    val address: Address?,
)
