package robert.findtransport.domain.usecase.network

interface CheckInternetUseCase {
  suspend fun isResolveIp(): Boolean

  fun isInternetConnected(): Boolean

  fun isVpnConnected(): Boolean
}
