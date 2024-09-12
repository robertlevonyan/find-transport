package robert.findtransport.domain.usecase.database

interface DatabaseUseCase {
    suspend fun isDatabaseEmpty(): Boolean

    suspend fun areStopsEmpty(): Boolean

    suspend fun areTransportsEmpty(): Boolean

    suspend fun clearDb()
}