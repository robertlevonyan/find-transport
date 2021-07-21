package robert.findtransport.domain.usecase.network

interface CheckInternetUseCase {
  suspend fun isResolveIp(): Boolean

  suspend fun isInternetConnected(): Boolean

  suspend fun isWifiConnected(): Boolean
  
  suspend fun isMobileDataConnected(): Boolean

  suspend fun isVpnConnected(): Boolean
}
