@file:Suppress("DEPRECATION", "unused")

package robert.findtransport.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.google.android.play.core.missingsplits.MissingSplitsManagerFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import robert.findtransport.R

@HiltAndroidApp
class A2BApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {
  override fun onCreate() {
    if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
      Toast.makeText(this, R.string.error_app, Toast.LENGTH_SHORT).show()
      return
    }

    super.onCreate()

    registerActivityLifecycleCallbacks(this)
  }

  override fun onActivityCreated(activity: Activity, p1: Bundle?) = Unit

  override fun onActivityStarted(activity: Activity) = Unit

  override fun onActivityResumed(activity: Activity) {
    if (activity is MainActivity) {
      activity.resumedState.value = true
    }
  }

  override fun onActivityPaused(activity: Activity) {
    if (activity is MainActivity) {
      activity.resumedState.value = true
    }
  }

  override fun onActivityStopped(activity: Activity) = Unit

  override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) = Unit

  override fun onActivityDestroyed(activity: Activity) = Unit
}
