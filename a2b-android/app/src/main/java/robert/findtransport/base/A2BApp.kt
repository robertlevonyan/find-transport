
package robert.findtransport.base

import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.google.android.play.core.missingsplits.MissingSplitsManagerFactory
import dagger.hilt.android.HiltAndroidApp
import robert.findtransport.R

@HiltAndroidApp
class A2BApp : MultiDexApplication() {
  override fun onCreate() {
    if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
      Toast.makeText(this, R.string.error_app, Toast.LENGTH_SHORT).show()
      return
    }

    super.onCreate()
  }
}
