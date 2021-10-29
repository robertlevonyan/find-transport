package robert.findtransport.domain.usecase.database

import robert.findtransport.domain.repository.DatabaseRepository
import javax.inject.Inject

class DatabaseUseCaseImpl @Inject constructor(private val databaseRepository: DatabaseRepository) : DatabaseUseCase {
  override suspend fun isDatabaseEmpty(): Boolean =
      databaseRepository.getJoinsCount() == 0 &&
          databaseRepository.getLocationsCount() == 0 &&
          databaseRepository.getStopsCount() == 0 &&
          databaseRepository.getTransportsCount() == 0

  override suspend fun areStopsEmpty(): Boolean =
      databaseRepository.getStopsCount() == 0 || databaseRepository.getLocationsCount() == 0

  override suspend fun areTransportsEmpty(): Boolean =
      databaseRepository.getTransportsCount() == 0 || databaseRepository.getJoinsCount() == 0

  override suspend fun clearDb() =
    databaseRepository.clearDb()

}
