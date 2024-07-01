package robert.findtransport.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import robert.findtransport.data.cache.AppDatabase
import robert.findtransport.data.cache.HistoryDao
import robert.findtransport.data.cache.StopsDao
import robert.findtransport.data.cache.TransportsDao
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.data.model.error.A2bException
import robert.findtransport.data.service.AddressProviderService
import robert.findtransport.data.service.ApplicationContextProvider
import robert.findtransport.data.service.FusedLocationService
import robert.findtransport.data.service.InMemoryCacheService
import robert.findtransport.data.service.LocationObserverService
import robert.findtransport.data.service.ResourcesService
import robert.findtransport.data.service.SharedPreferencesService

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
  fun jsonFeature() = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
  }

  @Provides
  fun getHttpClient(json: Json) = HttpClient(Android) {
    engine {
      connectTimeout = 100_000
      socketTimeout = 100_000
    }
    install(ContentNegotiation) {
      json(json)
    }
    expectSuccess = true
    HttpResponseValidator {
      handleResponseExceptionWithRequest { exception, request ->
        val clientException =
          exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
        val exceptionResponse = clientException.response
        if (exceptionResponse.status == HttpStatusCode.NotFound) {
          throw A2bException(ExceptionType.API, -1, clientException)
        }
      }
    }
  }
}
