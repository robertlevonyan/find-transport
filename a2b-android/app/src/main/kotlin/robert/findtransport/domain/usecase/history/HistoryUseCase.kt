package robert.findtransport.domain.usecase.history

import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.History

interface HistoryUseCase {
    fun getHistory(): Flow<List<History>>

    suspend fun clearHistory()

    suspend fun removeHistoryItem(id: Int)

    suspend fun saveInHistory(history: History)
}