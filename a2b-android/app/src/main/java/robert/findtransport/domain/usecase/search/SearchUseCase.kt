package robert.findtransport.domain.usecase.search

import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.enums.SearchState

interface SearchUseCase {
  suspend fun search(fromId: Int, toId: Int, opened: String): Flow<SearchState>
}
