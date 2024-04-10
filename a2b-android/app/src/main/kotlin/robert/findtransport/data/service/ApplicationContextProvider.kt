package robert.findtransport.data.service

import android.content.Context

class ApplicationContextProvider(private val context: Context) {
  fun getApplicationContext(): Context = context.applicationContext
}
