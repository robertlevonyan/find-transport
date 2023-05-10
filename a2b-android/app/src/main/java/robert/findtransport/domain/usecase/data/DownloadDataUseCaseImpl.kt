package robert.findtransport.domain.usecase.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import robert.findtransport.data.model.enums.DataLoading
import robert.findtransport.data.model.Result
import robert.findtransport.data.model.error.DataDownloadExceptions
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.feedback.FeedbackUseCase
import robert.findtransport.domain.usecase.network.CheckInternetUseCase
import robert.findtransport.domain.usecase.preference.VersionUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import java.io.EOFException
import javax.inject.Inject

class DownloadDataUseCaseImpl @Inject constructor(
  private val checkInternetUseCase: CheckInternetUseCase,
  private val versionUseCase: VersionUseCase,
  private val databaseUseCase: DatabaseUseCase,
  private val transportUseCase: TransportUseCase,
  private val stopsUseCase: StopsUseCase,
  private val feedbackUseCase: FeedbackUseCase,
) : DownloadDataUseCase {
  override fun downloadData(): Flow<DataLoading> = flow {
    emit(DataLoading.Loading)
    delay(1000)

    if (checkInternetUseCase.isVpnConnected()) {
      throw DataDownloadExceptions.VpnException()
    }

    if (!checkInternetUseCase.isResolveIp() || !checkInternetUseCase.isInternetConnected()) {
      if (databaseUseCase.isDatabaseEmpty()) {
        feedbackUseCase.sendFeedback("error@a2b.com", "Data download", """
          No Internet
        """.trimIndent())
        throw DataDownloadExceptions.NoInternetException()
      } else {
        emit(DataLoading.Loaded)
        delay(1000)
        return@flow
      }
    }

    if (!versionUseCase.isNewerVersion() && !databaseUseCase.isDatabaseEmpty()) {
      emit(DataLoading.Loaded)
      delay(1000)
      return@flow
    }

    try {
      if (!databaseUseCase.isDatabaseEmpty()) {
        databaseUseCase.clearDb()
      }

      checkForException(transportUseCase.downloadTransports())
      checkForException(transportUseCase.downloadJoins())
      checkForException(stopsUseCase.downloadStops())
      checkForException(stopsUseCase.downloadLocations())
      emit(DataLoading.Loaded)
    } catch (e: Exception) {
      val message = e.message ?: ""
      throw if (message.contains("database or disk is full (code 13)")) {
        feedbackUseCase.sendFeedback("error@a2b.com", "Data download", """
          Not enough space
          $message
        """.trimIndent())
        DataDownloadExceptions.NotEnoughSpaceException()
      } else {
        e
      }
    }
  }.flowOn(Dispatchers.IO)

  override fun forceDownloadData(): Flow<DataLoading> = flow {
    emit(DataLoading.Loading)
    delay(1000)

    if (checkInternetUseCase.isVpnConnected()) {
      feedbackUseCase.sendFeedback("error@a2b.com", "Data download", """
          VPN
        """.trimIndent())
      throw DataDownloadExceptions.VpnException()
    }

    if (!checkInternetUseCase.isResolveIp() || !checkInternetUseCase.isInternetConnected()) {
      throw DataDownloadExceptions.NoInternetException()
    }

    try {
      databaseUseCase.clearDb()
      checkForException(transportUseCase.downloadTransports())
      checkForException(transportUseCase.downloadJoins())
      checkForException(stopsUseCase.downloadStops())
      checkForException(stopsUseCase.downloadLocations())
      emit(DataLoading.Loaded)
      delay(1000)
    } catch (e: Exception) {
      val message = e.message ?: ""
      throw if (message.contains("database or disk is full (code 13)")) {
        feedbackUseCase.sendFeedback("error@a2b.com", "Data download", """
          Not enough space
          $message
        """.trimIndent())
        DataDownloadExceptions.NotEnoughSpaceException()
      } else {
        e
      }
    }
  }.flowOn(Dispatchers.IO)

  private suspend fun checkForException(result: Result<Unit>) {
    if (result is Result.Error) {
      if (result.exception.error is EOFException) {
        feedbackUseCase.sendFeedback("error@a2b.com", "Data download", """
          EOF Exception
          ${result.exception.error}
        """.trimIndent())
        throw DataDownloadExceptions.NotDownloadedException()
      } else {
        feedbackUseCase.sendFeedback("error@a2b.com", "Data download", """
          General error
          ${result.exception.error}
        """.trimIndent())
        throw result.exception
      }
    }
  }
}
