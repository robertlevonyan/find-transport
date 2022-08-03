package robert.findtransport.data.service

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import robert.findtransport.utils.PREFERENCES

class SharedPreferencesService private constructor(context: Context) {
  private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

  init {
    preferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
      sharedPreferences.all
        .getOrDefault(key, null)
        .let { value -> preferencesChangeFlow.value = key to value }
    }
  }

  fun putBoolean(key: String, value: Boolean) {
    preferences.edit().putBoolean(key, value).apply()
  }

  fun putInt(key: String, value: Int) {
    preferences.edit().putInt(key, value).apply()
  }

  fun putString(key: String, value: String) {
    preferences.edit().putString(key, value).apply()
  }

  fun getBoolean(key: String, defValue: Boolean): Boolean {
    return preferences.getBoolean(key, defValue)
  }

  fun getInt(key: String, defValue: Int): Int {
    return preferences.getInt(key, defValue)
  }

  fun getString(key: String, defValue: String): String? {
    return preferences.getString(key, defValue)
  }

  companion object {
    val preferencesChangeFlow = MutableStateFlow<Pair<String, Any?>>("" to null)

    inline fun <reified T : Any> getPreferenceChangedValue(key: String): Flow<T> = preferencesChangeFlow.map { pair ->
      val nextKey = pair.first
      val value = pair.second

      if (value != null && key.isNotEmpty() && nextKey == key) {
        value as T
      } else {
        null
      }
    }
      .filterNotNull()
      .flowOn(Dispatchers.IO)

    fun getPreferences(context: Context): SharedPreferencesService {
      return SharedPreferencesService(context)
    }
  }
}
