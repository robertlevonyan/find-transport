package robert.findtransport.domain.repository

import robert.findtransport.data.entity.History

interface HistoryRepository {

  suspend fun getHistory(): List<History>

  suspend fun clearHistory()

  suspend fun removeHistoryItem(id: Int)

  suspend fun saveInHistory(history: History)

}
