package robert.findtransport.data.repository

import robert.findtransport.data.cache.HistoryDao
import robert.findtransport.data.entity.History
import robert.findtransport.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(private val historyDao: HistoryDao) : HistoryRepository {
  override suspend fun getHistory(): List<History> =
      historyDao.getHistory()

  override suspend fun clearHistory() =
      historyDao.clearHistory()

  override suspend fun removeHistoryItem(id: Int) =
      historyDao.removeHistoryItem(id)

  override suspend fun saveInHistory(history: History) =
      historyDao.insert(history)
}