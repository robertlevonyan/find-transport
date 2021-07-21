package robert.findtransport.domain.usecase.history

import robert.findtransport.data.model.History

interface HistoryUseCase {
  suspend fun getHistory(): List<History>

  suspend fun clearHistory()

  suspend fun removeHistoryItem(id: Int)

  suspend fun saveInHistory(history: History)
}