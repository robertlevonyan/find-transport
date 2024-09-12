package robert.findtransport.domain.usecase.history

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
    override fun getHistory(): Flow<List<History>> =
        historyRepo.getHistory().map { historyList ->
            historyList.map { apiHistory ->
                val fromId = apiHistory.fromStopId ?: 0
                val toId = apiHistory.toStopId ?: 0

                apiHistory.toHistory(stopsUseCase.getStop(fromId), stopsUseCase.getStop(toId))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyRepo.clearHistory()
    }

    override suspend fun removeHistoryItem(id: Int) = withContext(Dispatchers.IO) {
        historyRepo.removeHistoryItem(id)
    }

    override suspend fun saveInHistory(history: History) = withContext(Dispatchers.IO) {
        historyRepo.saveInHistory(history.toApiHistory())
    }
}