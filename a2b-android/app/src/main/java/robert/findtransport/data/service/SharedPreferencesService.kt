package robert.findtransport.data.service

import android.content.Context
import robert.findtransport.utils.PREFERENCES

class SharedPreferencesService private constructor(context: Context) {
  private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
  
  companion object {
    fun getPreferences(context: Context): SharedPreferencesService {
      return SharedPreferencesService(context)
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
}
