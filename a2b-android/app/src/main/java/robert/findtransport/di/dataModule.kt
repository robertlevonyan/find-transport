package robert.findtransport.di

import android.content.Context
import android.net.ConnectivityManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import robert.findtransport.data.api.ApiService
import robert.findtransport.data.api.RetrofitClient
import robert.findtransport.data.cache.AppDatabase
import robert.findtransport.data.service.*

val dataModule = module {
  single { SharedPreferencesService.getPreferences(get()) }

  single { AppDatabase.getInstance(get()) }

  single<ApiService> { RetrofitClient.getClient().create(ApiService::class.java) }

  single { get<AppDatabase>().stopsDao() }

  single { get<AppDatabase>().transportsDao() }

  single { get<AppDatabase>().historyDao() }

  single { ResourcesService(get()) }

  single { FusedLocationService(get()) }

  single { LocationObserverService(get()) }

  single { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

  single { InMemoryCacheService }

  single { MapboxNavigationService() }
}
