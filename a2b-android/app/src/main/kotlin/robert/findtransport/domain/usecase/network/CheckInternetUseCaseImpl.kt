package robert.findtransport.domain.usecase.network

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import robert.findtransport.BuildConfig
import java.net.InetAddress
import java.net.UnknownHostException
import javax.inject.Inject

@SuppressLint("MissingPermission")
class CheckInternetUseCaseImpl @Inject constructor(
  private val connectivityManager: ConnectivityManager
) : CheckInternetUseCase {
  @Suppress("BlockingMethodInNonBlockingContext")
  override fun isResolveIp(): Boolean = try {
    InetAddress.getByName(BuildConfig.IP_ADDRESS).hostName
    true
  } catch (e: UnknownHostException) {
    e.printStackTrace()
    false
  }

  override fun isInternetConnected(): Boolean = connectivityManager.run {
    @Suppress("DEPRECATION")
    connectivityManager.activeNetworkInfo?.run {
      when (type) {
        ConnectivityManager.TYPE_WIFI -> true
        ConnectivityManager.TYPE_MOBILE -> true
        ConnectivityManager.TYPE_ETHERNET -> true
        else -> false
      }
    }
  } ?: false

  @Suppress("DEPRECATION")
  override fun isVpnConnected(): Boolean {
    val networks: Array<Network> = connectivityManager.allNetworks

    for (i in networks.indices) {
      val capabilities: NetworkCapabilities = connectivityManager.getNetworkCapabilities(networks[i]) ?: continue
      val isVpn = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

      if (isVpn) return true
    }
    return false
  }
}
