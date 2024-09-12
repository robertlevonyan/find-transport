package robert.findtransport.data.cache

import androidx.room.TypeConverter
import com.google.gson.Gson
import robert.findtransport.data.entity.TransportRoute

class TransportRouteTypeConverter {
    @TypeConverter
    fun fromTransportRoute(route: TransportRoute?): String? {
        if (route == null) return null
        return Gson().toJson(route, TransportRoute::class.java)
    }

    @TypeConverter
    fun toTransportRoute(json: String?): TransportRoute? {
        if (json == null) return null
        return Gson().fromJson(json, TransportRoute::class.java)
    }
}

