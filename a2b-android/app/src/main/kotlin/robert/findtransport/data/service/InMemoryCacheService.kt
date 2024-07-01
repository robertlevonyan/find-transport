@file:Suppress("UNCHECKED_CAST")

package robert.findtransport.data.service

object InMemoryCacheService {

  private val cacheMap = mutableMapOf<String, Any>()

  fun <T> save(data: Pair<String, T>) {
    cacheMap[data.first] = data.second ?: Unit
  }

  fun <T> get(key: String): T? =
      cacheMap[key] as T?
}