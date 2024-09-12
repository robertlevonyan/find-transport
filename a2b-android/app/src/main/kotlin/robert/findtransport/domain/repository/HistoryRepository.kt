package robert.findtransport.domain.repository

import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.entity.History

interface HistoryRepository {
    fun getHistory(): Flow<List<History>>

    suspend fun clearHistory()

    suspend fun removeHistoryItem(id: Int)

    suspend fun saveInHistory(history: History)
}
