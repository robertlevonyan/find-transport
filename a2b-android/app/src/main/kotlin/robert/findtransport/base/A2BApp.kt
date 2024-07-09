package robert.findtransport.base

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import robert.findtransport.BuildConfig
import robert.findtransport.presentation.reusables.ExceptionListener
import robert.findtransport.presentation.reusables.activity.ExceptionActivity

@HiltAndroidApp
class A2BApp : MultiDexApplication(), ExceptionListener {
    override fun onCreate() {
        if (!BuildConfig.DEBUG) {
            setupExceptionHandler()
        }
        super.onCreate()
    }

    private fun setupExceptionHandler() {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            uncaughtException(t, e)
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        startActivity(Intent(this, ExceptionActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
