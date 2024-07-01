package robert.findtransport.domain.usecase.search

import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.enums.SearchState

interface NewSearchUseCase {
  suspend fun invoke(
    originName: String,
    originLatitude: Float,
    originLongitude: Float,
    destinationName: String,
    destinationLatitude: Float,
    destinationLongitude: Float,
    opened: String
  ): Flow<SearchState>
}
