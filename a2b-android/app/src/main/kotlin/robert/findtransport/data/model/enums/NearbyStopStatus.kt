package robert.findtransport.data.model.enums

import robert.findtransport.data.model.Stop

sealed class NearbyStopStatus {
    object Idle : NearbyStopStatus()
    object Loading : NearbyStopStatus()
    object Failed : NearbyStopStatus()
    class NearbyStop(val stop: Stop) : NearbyStopStatus()
}
