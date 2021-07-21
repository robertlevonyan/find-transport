@file:Suppress("DEPRECATION", "unused")

package robert.findtransport.base

import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.google.android.play.core.missingsplits.MissingSplitsManagerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import robert.findtransport.R
import robert.findtransport.di.dataModule
import robert.findtransport.di.presenterModule
import robert.findtransport.di.repositoryModule
import robert.findtransport.di.useCaseModule

class A2BApp : MultiDexApplication() {
  override fun onCreate() {
    if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
      Toast.makeText(this, R.string.error_app, Toast.LENGTH_SHORT).show()
      return
    }

    super.onCreate()
    startKoin {
      androidContext(this@A2BApp)
      modules(listOf(
          dataModule,
          repositoryModule,
          useCaseModule,
          presenterModule
      ))
    }
  }
}
