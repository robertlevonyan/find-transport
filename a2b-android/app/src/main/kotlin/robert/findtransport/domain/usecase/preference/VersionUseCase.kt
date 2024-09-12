package robert.findtransport.domain.usecase.preference

interface VersionUseCase {
    suspend fun isNewerVersion(): Boolean
}
