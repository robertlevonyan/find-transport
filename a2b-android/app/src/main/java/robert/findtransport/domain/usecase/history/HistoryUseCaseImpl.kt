package robert.findtransport.domain.usecase.history

import robert.findtransport.data.model.History
import robert.findtransport.domain.mapper.toApiHistory
import robert.findtransport.domain.mapper.toHistory
import robert.findtransport.domain.repository.HistoryRepository
import robert.findtransport.domain.usecase.stop.StopsUseCase
import javax.inject.Inject

class HistoryUseCaseImpl @Inject constructor(
    private val historyRepo: HistoryRepository,
    private val stopsUseCase: StopsUseCase,
) : HistoryUseCase {
  override suspend fun getHistory(): List<History> =
      historyRepo.getHistory().map { apiHistory ->
        val fromId = apiHistory.fromStopId ?: 0
        val toId = apiHistory.toStopId ?: 0

        apiHistory.toHistory(stopsUseCase.getStop(fromId), stopsUseCase.getStop(toId))
      }

  override suspend fun clearHistory() =
      historyRepo.clearHistory()

  override suspend fun removeHistoryItem(id: Int) =
      historyRepo.removeHistoryItem(id)

  override suspend fun saveInHistory(history: History) =
      historyRepo.saveInHistory(history.toApiHistory())
}