package robert.findtransport.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import robert.findtransport.domain.usecase.data.DownloadDataUseCase
import robert.findtransport.domain.usecase.data.DownloadDataUseCaseImpl
import robert.findtransport.domain.usecase.database.DatabaseUseCase
import robert.findtransport.domain.usecase.database.DatabaseUseCaseImpl
import robert.findtransport.domain.usecase.feedback.FeedbackUseCase
import robert.findtransport.domain.usecase.feedback.FeedbackUseCaseImpl
import robert.findtransport.domain.usecase.history.HistoryUseCase
import robert.findtransport.domain.usecase.history.HistoryUseCaseImpl
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.location.LocationUseCaseImpl
import robert.findtransport.domain.usecase.network.CheckInternetUseCase
import robert.findtransport.domain.usecase.network.CheckInternetUseCaseImpl
import robert.findtransport.domain.usecase.permission.PermissionUseCase
import robert.findtransport.domain.usecase.permission.PermissionUseCaseImpl
import robert.findtransport.domain.usecase.preference.*
import robert.findtransport.domain.usecase.rate.RateUseCase
import robert.findtransport.domain.usecase.rate.RateUseCaseImpl
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCaseImpl
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class, ActivityComponent::class)
abstract class UseCaseModule {
  @Binds
  abstract fun bindCheckInternetUseCase(checkInternetUseCaseImpl: CheckInternetUseCaseImpl): CheckInternetUseCase

  @Binds
  abstract fun bindVersionUseCase(versionUseCaseImpl: VersionUseCaseImpl): VersionUseCase

  @Binds
  abstract fun bindDatabaseUseCase(databaseUseCaseImpl: DatabaseUseCaseImpl): DatabaseUseCase

  @Binds
  abstract fun bindThemeUseCase(themeUseCaseImpl: ThemeUseCaseImpl): ThemeUseCase

  @Binds
  abstract fun bindLocaleUseCase(localeUseCaseImpl: LocaleUseCaseImpl): LocaleUseCase

  @Binds
  abstract fun bindIntroUseCase(introUseCaseImpl: IntroUseCaseImpl): IntroUseCase

  @Binds
  abstract fun bindFeedbackUseCase(feedbackUseCaseImpl: FeedbackUseCaseImpl): FeedbackUseCase

  @Binds
  abstract fun bindStopsUseCase(stopsUseCaseImpl: StopsUseCaseImpl): StopsUseCase

  @Binds
  abstract fun bindTransportUseCase(transportUseCaseImpl: TransportUseCaseImpl): TransportUseCase

  @Binds
  abstract fun bindPermissionUseCase(permissionUseCaseImpl: PermissionUseCaseImpl): PermissionUseCase

  @Binds
  abstract fun bindHistoryUseCase(historyUseCaseImpl: HistoryUseCaseImpl): HistoryUseCase

  @Binds
  abstract fun bindRateUseCase(rateUseCaseImpl: RateUseCaseImpl): RateUseCase

  @Binds
  abstract fun bindLocationUseCase(locationUseCaseImpl: LocationUseCaseImpl): LocationUseCase

  @Binds
  abstract fun bindDownloadDataUseCase(downloadDataUseCaseImpl: DownloadDataUseCaseImpl): DownloadDataUseCase
}
