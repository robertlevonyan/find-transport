package robert.findtransport.domain.usecase.search

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import robert.findtransport.data.entity.History
import robert.findtransport.data.model.RouteSearchCase
import robert.findtransport.data.model.RouteSearchElementType
import robert.findtransport.data.model.RouteSearchResult
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.domain.repository.HistoryRepository
import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.presentation.screens.search.SearchOpenInitiator
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.intersectTransports
import java.util.Date
import javax.inject.Inject

class NewSearchUseCaseImpl2 @Inject constructor(
  private val historyRepository: HistoryRepository,
  private val localeRepository: LocaleRepository,
  private val stopsUseCase: StopsUseCase,
  private val transportsUseCase: TransportUseCase,
) : NewSearchUseCase {

  override suspend fun invoke(
    originName: String,
    originLatitude: Float,
    originLongitude: Float,
    destinationName: String,
    destinationLatitude: Float,
    destinationLongitude: Float,
    opened: String
  ): Flow<SearchState> = flow {
    emit(SearchState.Searching)

    val originLocation = Location(originName).apply {
      latitude = originLatitude.toDouble()
      longitude = originLongitude.toDouble()
    }
    val originStops = stopsUseCase.getNearbyStops(location = originLocation)

    val destinationLocation = Location(destinationName).apply {
      latitude = destinationLatitude.toDouble()
      longitude = destinationLongitude.toDouble()
    }
    val destinationStops = stopsUseCase.getNearbyStops(location = destinationLocation)

    if (originStops.isNotEmpty() && destinationStops.isNotEmpty()) {
      for ((originIndex, intermediateOriginStop) in originStops.withIndex()) {
        val destinationSize = if (destinationStops.size < SEARCH_THRESHOLD) {
          destinationStops.size
        } else {
          SEARCH_THRESHOLD
        }

        for (destinationIndex in 0 until destinationSize) {
          val intermediateDestinationStop = destinationStops[destinationIndex]
          val transports = searchRoutes(
            originStop = intermediateOriginStop,
            destinationStop = intermediateDestinationStop,
          )

          if (transports.isNotEmpty()) {
            val result = mutableListOf<RouteSearchResult>()
            if (originIndex != 0) {
              result.add(
                RouteSearchResult(
                  type = RouteSearchElementType.WALK_FROM,
                  walkDestination = originName,
                  case = RouteSearchCase.SINGLE_FROM,
                )
              )
            }
            result.add(
              RouteSearchResult(
                type = RouteSearchElementType.TRANSPORT_TITLE,
                stop = intermediateOriginStop,
                case = RouteSearchCase.SINGLE_FROM
              )
            )
            transports.forEach { transport ->
              result.add(
                RouteSearchResult(
                  type = RouteSearchElementType.TRANSPORT,
                  transport = transport,
                  case = RouteSearchCase.SINGLE_FROM
                )
              )
            }
            result.add(
              RouteSearchResult(
                type = RouteSearchElementType.INTERCHANGE_TO,
                stop = intermediateDestinationStop,
                case = RouteSearchCase.SINGLE_FROM
              )
            )
            if (destinationIndex != 0) {
              result.add(
                RouteSearchResult(
                  type = RouteSearchElementType.WALK_TO,
                  walkDestination = destinationName,
                  case = RouteSearchCase.SINGLE_FROM,
                )
              )
            }
            emit(SearchState.Result(result = result))
            return@flow
          }
        }
      }
    }

    emit(SearchState.Failed(ExceptionType.EMPTY_SEARCH))
  }.flowOn(Dispatchers.IO)

  private suspend fun searchRoutes(
    originStop: Stop,
    destinationStop: Stop,
  ): List<Transport> {
    val originTransports = transportsUseCase.getTransportsForStop(originStop.id)
    val destinationTransports = transportsUseCase.getTransportsForStop(destinationStop.id)

    return originTransports.intersectTransports(destinationTransports)
  }

  private suspend fun createResults(
    transports: List<Transport>,
    originName: String,
    destinationName: String,
    intermediateOriginStop: Stop,
    intermediateDestinationStop: Stop,
    opened: String,
    originLocation: Location,
    destinationLocation: Location,
  ): List<RouteSearchResult> {
    val results = buildList {
      val currentLocale = localeRepository.getCurrentLanguage()

      if (originName != intermediateOriginStop.getCurrentName(currentLocale)) {
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
          stop = intermediateOriginStop,
          case = RouteSearchCase.SINGLE_FROM
        )
      )

      for (transport in transports) {
        add(
          RouteSearchResult(
            type = RouteSearchElementType.TRANSPORT,
            transport = transport,
            case = RouteSearchCase.SINGLE_FROM
          )
        )
      }
      add(
        RouteSearchResult(
          type = RouteSearchElementType.INTERCHANGE_TO,
          stop = intermediateDestinationStop,
          case = RouteSearchCase.SINGLE_FROM
        )
      )

      if (destinationName != intermediateDestinationStop.getCurrentName(currentLocale)) {
        add(
          RouteSearchResult(
            type = RouteSearchElementType.WALK_TO,
            walkDestination = destinationName,
            case = RouteSearchCase.SINGLE_FROM,
          )
        )
      }
    }

    tryToSaveToHistory(
      opened = opened,
      originId = intermediateOriginStop.id,
      destinationId = intermediateDestinationStop.id,
      originName = originName,
      destinationName = destinationName,
      originLatitude = originLocation.latitude.toFloat(),
      originLongitude = originLocation.longitude.toFloat(),
      destinationLatitude = destinationLocation.latitude.toFloat(),
      destinationLongitude = destinationLocation.longitude.toFloat(),
    )

    return results
  }

  private suspend fun tryToSaveToHistory(
    opened: String,
    originId: Int,
    destinationId: Int,
    originName: String,
    destinationName: String,
    originLatitude: Float,
    originLongitude: Float,
    destinationLatitude: Float,
    destinationLongitude: Float,
  ) {
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

  companion object {
    private const val SEARCH_THRESHOLD = 5
  }
}
