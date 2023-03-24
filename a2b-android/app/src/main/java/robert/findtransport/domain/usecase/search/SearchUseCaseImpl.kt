package robert.findtransport.domain.usecase.search

import android.location.Location
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import robert.findtransport.data.entity.History
import robert.findtransport.data.model.*
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.domain.mapper.toTransport
import robert.findtransport.domain.repository.HistoryRepository
import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.presentation.screens.search.SearchOpenInitiator
import robert.findtransport.utils.extensions.getCurrentName
import java.util.*
import javax.inject.Inject

class SearchUseCaseImpl @Inject constructor(
  private val transportsRepository: TransportsRepository,
  private val historyRepository: HistoryRepository,
  private val localeRepository: LocaleRepository,
  private val stopsUseCase: StopsUseCase,
) : SearchUseCase {

  override suspend fun search(
    originName: String,
    originLatitude: Float,
    originLongitude: Float,
    destinationName: String,
    destinationLatitude: Float,
    destinationLongitude: Float,
    opened: String,
  ): Flow<SearchState> = flow {
    emit(SearchState.Searching)

    val currentLocale = localeRepository.getCurrentLanguage()
    val originLocation = Location(originName).apply {
      latitude = originLatitude.toDouble()
      longitude = originLongitude.toDouble()
    }
    val originStop = stopsUseCase.getNearbyStop(originLocation)
    val destinationLocation = Location(destinationName).apply {
      latitude = destinationLatitude.toDouble()
      longitude = destinationLongitude.toDouble()
    }
    val destinationStop = stopsUseCase.getNearbyStop(destinationLocation)

    val saveToHistory = try {
      SearchOpenInitiator.valueOf(opened).let { initiator ->
        when (initiator) {
          SearchOpenInitiator.HOME -> true
          SearchOpenInitiator.HISTORY -> false
        }
      }
    } catch (e: Exception) {
      false
    }

    val fromTransports = transportsRepository.getTransportsForStop(originStop.id)
    val toTransports = transportsRepository.getTransportsForStop(destinationStop.id)

    val foundTransports: List<Transport> = searchTransports(fromTransports, toTransports)

    if (foundTransports.isEmpty()) {
      val stops = stopsUseCase.getStops()
      val fromNearby: List<Stop> = getNearbyFor(originStop, stops)
      val toNearby: List<Stop> = getNearbyFor(destinationStop, stops)

      val multiResult = mutableListOf<RouteSearchResult>()

      // get routes from origin point to a nearby point of destination
      val (multiDataTo, interchangeStopTo) = tryFindRoutes(toNearby, fromTransports)
      val toResults = createToResult(
        multiDataTo = multiDataTo,
        interchangeStopTo = interchangeStopTo,
        originName = originName,
        originStop = originStop,
        destinationStop = destinationStop,
        destinationName = destinationName,
        currentLocale = currentLocale,
      )
      multiResult.addAll(toResults)

      if (multiResult.isNotEmpty()) {
        checkAndSaveToHistory(
          saveToHistory = saveToHistory,
          originId = originStop.id,
          destinationId = destinationStop.id,
          originName = originName,
          destinationName = destinationName,
          originLatitude = originLatitude,
          originLongitude = originLongitude,
          destinationLatitude = destinationLatitude,
          destinationLongitude = destinationLongitude,
        )
        emit(SearchState.Result(multiResult))
        return@flow
      }

      // get routes from a nearby point of origin to the destination
      val (multiDataFrom, interchangeStopFrom) = tryFindRoutes(fromNearby, toTransports)
      val fromResults = createFromResult(
        multiDataFrom = multiDataFrom,
        interchangeStopFrom = interchangeStopFrom,
        originStop = originStop,
        originName = originName,
        destinationStop = destinationStop,
        destinationName = destinationName,
        currentLocale = currentLocale,
      )
      multiResult.addAll(fromResults)

      if (multiResult.isNotEmpty()) {
        checkAndSaveToHistory(
          saveToHistory = saveToHistory,
          originId = originStop.id,
          destinationId = destinationStop.id,
          originName = originName,
          destinationName = destinationName,
          originLatitude = originLatitude,
          originLongitude = originLongitude,
          destinationLatitude = destinationLatitude,
          destinationLongitude = destinationLongitude,
        )
        emit(SearchState.Result(multiResult))
        return@flow
      }

      // get routes from a nearby point of origin to a nearby point of destination
      val fromNearbyAll: List<Stop> = getNearbyFor(originStop, stops)
      val toNearbyAll: List<Stop> = getNearbyFor(destinationStop, stops)

      topFor@ for (i in 1..fromNearbyAll.lastIndex) {
        val fromNear = fromNearbyAll[i]
        for (j in 1..toNearbyAll.lastIndex) {
          val toNear = toNearbyAll[j]
          if (fromNear.id == toNear.id) {
            val from2Transports = transportsRepository.getTransportsForStop(originStop.id)
            val to2Transports = transportsRepository.getTransportsForStop(toNear.id)
            val found2Transports: List<Transport> = searchTransports(from2Transports, to2Transports)
            if (found2Transports.isNotEmpty()) {
              val from3Transports = transportsRepository.getTransportsForStop(fromNear.id)
              val to3Transports = transportsRepository.getTransportsForStop(destinationStop.id)
              val found3Transports: List<Transport> =
                searchTransports(from3Transports, to3Transports)
              val result = createMultiChangeResult(
                found2Transports = found2Transports,
                found3Transports = found3Transports,
                originName = originName,
                originStop = originStop,
                destinationName = destinationName,
                destinationStop = destinationStop,
                currentLocale = currentLocale,
                fromNear = fromNear,
              )
              multiResult.addAll(result)
              break@topFor
            }
          }
        }
      }
      checkAndSaveToHistory(
        saveToHistory = saveToHistory,
        originId = originStop.id,
        destinationId = destinationStop.id,
        originName = originName,
        destinationName = destinationName,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude,
      )
      emit(SearchState.Result(multiResult))
    } else {
      //return routes found
      checkAndSaveToHistory(
        saveToHistory = saveToHistory,
        originId = originStop.id,
        destinationId = destinationStop.id,
        originName = originName,
        destinationName = destinationName,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude,
      )

      val result = createFoundTransportsResult(
        originName = originName,
        originStop = originStop,
        currentLocale = currentLocale,
        foundTransports = foundTransports,
        destinationName = destinationName,
        destinationStop = destinationStop,
      )
      emit(SearchState.Result(result))
    }
  }.flowOn(Dispatchers.IO)

  private fun searchTransports(
    fromT: List<robert.findtransport.data.entity.Transport>,
    toT: List<robert.findtransport.data.entity.Transport>
  ): List<Transport> = buildList {
    for (apiTransport in fromT) {
      val toFormatted = toT.map { it.id }
      if (toFormatted.contains(apiTransport.id)) {
        add(
          apiTransport.toTransport(transportsRepository.getTransportStops(
            apiTransport.id
              ?: 0
          ).map { it.toStop() })
        )
      }
    }
  }.sortedBy { it.id }

  private suspend fun getNearbyFor(stop: Stop, stops: List<Stop>): List<Stop> = buildList {
    val coordinates = stop.coordinates
    val fromLocation = coordinates.first()
    addAll(getNearbyStops(fromLocation, stops))
  }

  private fun getNearbyStops(
    currentStop: StopLocation,
    stops: List<Stop>,
  ): Sequence<Stop> = buildList {
    for (stop in stops) {
      for (coordinate in stop.coordinates) {
        val newLocation = Location("next").apply {
          latitude = coordinate.lat
          longitude = coordinate.lng
        }

        add(
          NearbyLocation(
            stop.id,
            newLocation.latitude,
            newLocation.longitude,
            Location("stop").apply {
              latitude = currentStop.lat
              longitude = currentStop.lng
            }.distanceTo(newLocation)
          )
        )
      }
    }
  }.asSequence()
    .sortedBy { it.locationDistance }
    .map { runBlocking { stopsUseCase.getStop(it.stopId) } }

  private suspend fun tryFindRoutes(
    nearby: List<Stop>,
    transports: List<robert.findtransport.data.entity.Transport>,
  ): Pair<Set<Transport>, Stop?> = withContext(Dispatchers.IO) {
    val multiData = mutableSetOf<Transport>()
    var interchangeStop: Stop? = null

    for (stop in nearby) {
      multiData.addAll(
        searchTransports(
          transports,
          transportsRepository.getTransportsForStop(stop.id)
        )
      )
      if (multiData.isNotEmpty()) {
        interchangeStop = stop
        break
      }
    }

    return@withContext multiData to interchangeStop
  }

  private fun createToResult(
    multiDataTo: Set<Transport>,
    interchangeStopTo: Stop?,
    originName: String,
    originStop: Stop,
    destinationStop: Stop,
    destinationName: String,
    currentLocale: String,
  ): Collection<RouteSearchResult> = buildList {
    if (multiDataTo.isEmpty()) return@buildList
    if (originName != originStop.getCurrentName(currentLocale)) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_FROM,
          walkDestination = originName,
          case = RouteSearchCase.SINGLE_FROM,
        )
      )
    }

    add(
      RouteSearchResult(
        type = RouteSearchElementType.TRANSPORT_TITLE,
        stop = originStop,
        case = RouteSearchCase.SINGLE_FROM
      )
    )
    for (t in multiDataTo) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.TRANSPORT,
          transport = t,
          case = RouteSearchCase.SINGLE_FROM
        )
      )
    }
    add(
      RouteSearchResult(
        type = RouteSearchElementType.INTERCHANGE_TO,
        stop = interchangeStopTo,
        case = RouteSearchCase.SINGLE_FROM
      )
    )

    val walkRoute = if (destinationName != destinationStop.getCurrentName(currentLocale)) {
      RouteSearchResult(
        type = RouteSearchElementType.WALK_TO,
        walkDestination = destinationName,
        case = RouteSearchCase.SINGLE_FROM,
      )
    } else {
      RouteSearchResult(
        type = RouteSearchElementType.WALK_TO,
        stop = destinationStop,
        case = RouteSearchCase.SINGLE_FROM
      )
    }
    add(walkRoute)
  }

  private fun createFromResult(
    multiDataFrom: Set<Transport>,
    interchangeStopFrom: Stop?,
    originStop: Stop,
    originName: String,
    destinationStop: Stop,
    destinationName: String,
    currentLocale: String,
  ): Collection<RouteSearchResult> = buildList {
    if (multiDataFrom.isEmpty()) return@buildList
    val walkRoute = if (originName != originStop.getCurrentName(currentLocale)) {
      RouteSearchResult(
        type = RouteSearchElementType.WALK_FROM,
        walkDestination = originName,
        case = RouteSearchCase.SINGLE_FROM,
      )
    } else {
      RouteSearchResult(
        type = RouteSearchElementType.WALK_FROM,
        stop = originStop,
        case = RouteSearchCase.SINGLE_TO
      )
    }
    add(walkRoute)

    add(
      RouteSearchResult(
        type = RouteSearchElementType.INTERCHANGE_FROM,
        stop = interchangeStopFrom,
        case = RouteSearchCase.SINGLE_TO
      )
    )
    for (t in multiDataFrom) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.TRANSPORT,
          transport = t,
          case = RouteSearchCase.SINGLE_TO
        )
      )
    }
    if (destinationName != destinationStop.getCurrentName(currentLocale)) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_TO,
          walkDestination = destinationName,
          case = RouteSearchCase.SINGLE_FROM,
        )
      )
    }
  }

  private fun createMultiChangeResult(
    found2Transports: List<Transport>,
    found3Transports: List<Transport>,
    originName: String,
    originStop: Stop,
    destinationName: String,
    destinationStop: Stop,
    currentLocale: String,
    fromNear: Stop,
  ) = buildList {
    if (found3Transports.isEmpty()) return@buildList

    if (originName != originStop.getCurrentName(currentLocale)) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_FROM,
          walkDestination = originName,
          case = RouteSearchCase.SINGLE_FROM,
        )
      )
    }
    add(
      RouteSearchResult(
        type = RouteSearchElementType.TRANSPORT_TITLE,
        stop = originStop,
        case = RouteSearchCase.FROM_TO
      )
    )
    for (t in found2Transports) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.TRANSPORT,
          transport = t,
          case = RouteSearchCase.FROM_TO
        )
      )
    }
    add(
      RouteSearchResult(
        type = RouteSearchElementType.INTERCHANGE_TO,
        stop = fromNear,
        case = RouteSearchCase.FROM_TO
      )
    )
    for (t in found3Transports) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.TRANSPORT,
          transport = t,
          case = RouteSearchCase.FROM_TO
        )
      )
    }
    add(
      RouteSearchResult(
        type = RouteSearchElementType.INTERCHANGE_TO,
        stop = destinationStop,
        case = RouteSearchCase.FROM_TO
      )
    )
    if (destinationName != destinationStop.getCurrentName(currentLocale)) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_TO,
          walkDestination = destinationName,
          case = RouteSearchCase.SINGLE_FROM,
        )
      )
    }
  }

  private fun createFoundTransportsResult(
    originName: String,
    originStop: Stop,
    currentLocale: String,
    foundTransports: List<Transport>,
    destinationName: String,
    destinationStop: Stop
  ): List<RouteSearchResult> = buildList {
    if (originName != originStop.getCurrentName(currentLocale)) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_FROM,
          walkDestination = originName,
          case = RouteSearchCase.SINGLE_FROM,
        )
      )
      add(
        RouteSearchResult(
          type = RouteSearchElementType.INTERCHANGE_FROM,
          stop = originStop,
          case = RouteSearchCase.FROM_TO,
        )
      )
    }
    foundTransports.forEach { transport ->
      add(
        RouteSearchResult(
          type = RouteSearchElementType.TRANSPORT,
          transport = transport,
          case = RouteSearchCase.FROM_TO
        )
      )
    }
    add(
      RouteSearchResult(
        type = RouteSearchElementType.INTERCHANGE_TO,
        stop = destinationStop,
        case = RouteSearchCase.SINGLE_TO,
      )
    )
    if (destinationName != destinationStop.getCurrentName(currentLocale)) {
      add(
        RouteSearchResult(
          type = RouteSearchElementType.WALK_TO,
          walkDestination = destinationName,
          case = RouteSearchCase.SINGLE_FROM,
        )
      )
    }
  }

  private suspend fun checkAndSaveToHistory(
    saveToHistory: Boolean,
    originId: Int,
    destinationId: Int,
    originName: String,
    destinationName: String,
    originLatitude: Float,
    originLongitude: Float,
    destinationLatitude: Float,
    destinationLongitude: Float,
  ) {
    if (!saveToHistory) return

    historyRepository.getHistory().firstOrNull()
      ?.any { history ->
        history.fromStopId == originId && history.toStopId == destinationId
      }?.let { contains ->
        if (!contains) {
          historyRepository.saveInHistory(
            History(
              fromStopId = originId,
              toStopId = destinationId,
              timestamp = Date().time,
              originName = originName,
              destinationName = destinationName,
              originLatitude = originLatitude,
              originLongitude = originLongitude,
              destinationLatitude = destinationLatitude,
              destinationLongitude = destinationLongitude,
            )
          )
        }
      }
  }
}