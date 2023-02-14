package robert.findtransport.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import robert.findtransport.data.api.ApiService
import robert.findtransport.data.api.RetrofitClient
import robert.findtransport.data.cache.AppDatabase
import robert.findtransport.data.cache.HistoryDao
import robert.findtransport.data.cache.StopsDao
import robert.findtransport.data.cache.TransportsDao
import robert.findtransport.data.service.*

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
object DataModule {
  @Provides
  fun getSharedPreferencesService(@ApplicationContext context: Context): SharedPreferencesService =
    SharedPreferencesService.getPreferences(context = context)

  @Provides
  fun getAppDatabase(@ApplicationContext context: Context): AppDatabase =
    AppDatabase.getInstance(context = context)

  @Provides
  fun getResourcesService(@ApplicationContext context: Context): ResourcesService =
    ResourcesService(context = context)

  @Provides
  fun getFusedLocationService(@ApplicationContext context: Context): FusedLocationService =
    FusedLocationService(context = context)

  @Provides
  fun getAddressProviderService(@ApplicationContext context: Context): AddressProviderService =
    AddressProviderService(context = context)

  @Provides
  fun getLocationObserverService(@ApplicationContext context: Context): LocationObserverService =
    LocationObserverService(context = context)

  @Provides
  fun getApplicationContextProvider(@ApplicationContext context: Context): ApplicationContextProvider =
    ApplicationContextProvider(context = context)

  @Provides
  fun getConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  @Provides
  fun getInMemoryCacheService(): InMemoryCacheService = InMemoryCacheService

  @Provides
  fun getStopsDao(appDatabase: AppDatabase): StopsDao = appDatabase.stopsDao()

  @Provides
  fun getTransportsDao(appDatabase: AppDatabase): TransportsDao = appDatabase.transportsDao()

  @Provides
  fun getHistoryDao(appDatabase: AppDatabase): HistoryDao = appDatabase.historyDao()

  @Provides
  fun getApiService(): ApiService = RetrofitClient.getClient().create(ApiService::class.java)
}
