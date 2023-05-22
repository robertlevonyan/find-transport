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
import robert.findtransport.domain.usecase.search.NewSearchUseCase
import robert.findtransport.domain.usecase.search.NewSearchUseCaseImpl
import robert.findtransport.domain.usecase.search.SearchUseCase
import robert.findtransport.domain.usecase.search.SearchUseCaseImpl
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCaseImpl
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class, ActivityComponent::class)
abstract class UseCaseModule {
  @Binds
  abstract fun bindCheckInternetUseCase(impl: CheckInternetUseCaseImpl): CheckInternetUseCase

  @Binds
  abstract fun bindVersionUseCase(impl: VersionUseCaseImpl): VersionUseCase

  @Binds
  abstract fun bindDatabaseUseCase(impl: DatabaseUseCaseImpl): DatabaseUseCase

  @Binds
  abstract fun bindThemeUseCase(impl: ThemeUseCaseImpl): ThemeUseCase

  @Binds
  abstract fun bindLocaleUseCase(impl: LocaleUseCaseImpl): LocaleUseCase

  @Binds
  abstract fun bindIntroUseCase(impl: IntroUseCaseImpl): IntroUseCase

  @Binds
  abstract fun bindFeedbackUseCase(impl: FeedbackUseCaseImpl): FeedbackUseCase

  @Binds
  abstract fun bindStopsUseCase(impl: StopsUseCaseImpl): StopsUseCase

  @Binds
  abstract fun bindTransportUseCase(impl: TransportUseCaseImpl): TransportUseCase

  @Binds
  abstract fun bindSearchUseCase(impl: SearchUseCaseImpl): SearchUseCase

  @Binds
  abstract fun bindNewSearchUseCase(impl: NewSearchUseCaseImpl): NewSearchUseCase

  @Binds
  abstract fun bindPermissionUseCase(impl: PermissionUseCaseImpl): PermissionUseCase

  @Binds
  abstract fun bindHistoryUseCase(impl: HistoryUseCaseImpl): HistoryUseCase

  @Binds
  abstract fun bindRateUseCase(impl: RateUseCaseImpl): RateUseCase

  @Binds
  abstract fun bindLocationUseCase(impl: LocationUseCaseImpl): LocationUseCase

  @Binds
  abstract fun bindDownloadDataUseCase(impl: DownloadDataUseCaseImpl): DownloadDataUseCase
}
