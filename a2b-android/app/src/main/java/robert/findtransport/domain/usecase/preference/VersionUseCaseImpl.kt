package robert.findtransport.domain.usecase.preference

import robert.findtransport.data.model.Result
import robert.findtransport.domain.repository.VersionRepository
import javax.inject.Inject

class VersionUseCaseImpl @Inject constructor(private val versionRepository: VersionRepository) : VersionUseCase {
  
  override suspend fun isNewerVersion(): Boolean {
    return try {
      val localVersion = versionRepository.getVersionFromCache()
      when (val remoteVersion = versionRepository.getVersionFromApi()) {
        is Result.Success -> {
          versionRepository.cacheVersion(remoteVersion.data)
          localVersion.replace("\"", "").toDouble() < remoteVersion.data.replace("\"", "").toDouble()
        }
        is Result.Error -> false
      }
    } catch (e: Exception) {
      false
    }
  }
}
