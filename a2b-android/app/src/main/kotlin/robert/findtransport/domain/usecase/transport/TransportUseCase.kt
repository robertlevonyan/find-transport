package robert.findtransport.domain.usecase.transport

import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport

interface TransportUseCase {
    fun getTransportById(id: Int): Flow<Transport>

    suspend fun getBuses(): List<Transport>

    suspend fun getMicrobuses(): List<Transport>

    suspend fun getTrolleybuses(): List<Transport>

    suspend fun getMetro(): List<Transport>

    suspend fun getTransportsForStop(id: Int): List<Transport>

    suspend fun downloadTransports(): Result<Unit>

    suspend fun downloadJoins(): Result<Unit>

    fun areTransportsCached(): Boolean

    fun areJoinsCached(): Boolean

    suspend fun toggleFavorite(transport: Transport)

    fun getNearbyStopFromTransport(
        transport: Transport,
        start: Stop,
        destination: Stop,
        location: Location,
        coroutineScope: CoroutineScope
    ): Flow<Pair<Stop, Stop>>

    var showOnlyFavorites: Boolean
}
