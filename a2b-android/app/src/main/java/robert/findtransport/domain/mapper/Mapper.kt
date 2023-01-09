package robert.findtransport.domain.mapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import robert.findtransport.data.model.History
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.StopLocation
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.data.entity.History as ApiHistory
import robert.findtransport.data.entity.Stop as ApiStop
import robert.findtransport.data.entity.StopLocation as ApiLocation
import robert.findtransport.data.entity.Transport as ApiTransport

fun ApiLocation.toStopLocation(apiStop: ApiStop): StopLocation = StopLocation(
  lat = lat ?: 0.0,
  lng = lng ?: 0.0,
  parentStop = apiStop.toStop(),
)

fun ApiStop.toStop(coordinates: List<StopLocation> = emptyList()): Stop = Stop(
  id = id ?: 0,
  nameAm = nameAm.orEmpty(),
  nameEn = nameEn.orEmpty(),
  nameRu = nameRu.orEmpty(),
  coordinates = coordinates,
)

fun Stop.toApiStop(): ApiStop = ApiStop(
  id = this@toApiStop.id,
  nameAm = this@toApiStop.nameAm,
  nameEn = this@toApiStop.nameEn,
  nameRu = this@toApiStop.nameRu,
)

fun ApiTransport.toTransport(
  stops: List<Stop>,
  reversedStops: List<Stop> = emptyList()
): Transport = Transport(
  id = id ?: 0,
  number = name.orEmpty(),
  type = TransportType.getByIndex(index = type?.minus(1) ?: TransportType.UNDEFINED.ordinal),
  stops = stops,
  stopsReversed = reversedStops,
  isFavorite = favorite,
)

fun ApiHistory.toHistory(fromStop: Stop, toStop: Stop) = History(
  id = id,
  fromStop = fromStop,
  toStop = toStop,
  results = results ?: 0,
  timestamp = timestamp ?: 0,
)

fun History.toApiHistory() = ApiHistory(
  fromStopId = fromStop.id,
  toStopId = toStop.id,
  results = results,
  timestamp = timestamp,
)

inline fun <reified T> T.toJson(): JsonObject {
  val gson = Gson()
  val json = gson.toJson(this, T::class.java)
  return gson.fromJson(json, JsonObject::class.java)
}

inline fun <reified T> JsonElement.fromJson(): T {
  val gson = Gson()
  val json = gson.toJson(this)
  return gson.fromJson(json, T::class.java)
}
