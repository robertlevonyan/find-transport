package robert.findtransport.domain.usecase.data

import kotlinx.coroutines.flow.Flow
import robert.findtransport.data.model.enums.DataLoading

interface DownloadDataUseCase {
  fun downloadData(): Flow<DataLoading>

  fun forceDownloadData(): Flow<DataLoading>
}