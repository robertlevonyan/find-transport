package robert.findtransport.domain.usecase.network

interface CheckInternetUseCase {
  fun isResolveIp(): Boolean

  fun isInternetConnected(): Boolean

  fun isVpnConnected(): Boolean
}
