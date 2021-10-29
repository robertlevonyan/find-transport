package robert.findtransport.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import robert.findtransport.data.repository.*
import robert.findtransport.domain.repository.*

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
abstract class RepositoryModule {

  @Binds
  abstract fun bindTransportsRepository(transportsRepositoryImpl: TransportsRepositoryImpl): TransportsRepository

  @Binds
  abstract fun bindStopsRepository(stopsRepositoryImpl: StopsRepositoryImpl): StopsRepository

  @Binds
  abstract fun bindVersionRepository(versionRepositoryImpl: VersionRepositoryImpl): VersionRepository

  @Binds
  abstract fun bindThemeRepository(themeRepositoryImpl: ThemeRepositoryImpl): ThemeRepository

  @Binds
  abstract fun bindLocaleRepository(localeRepositoryImpl: LocaleRepositoryImpl): LocaleRepository

  @Binds
  abstract fun bindResourcesRepository(resourcesRepositoryImpl: ResourcesRepositoryImpl): ResourcesRepository

  @Binds
  abstract fun bindIntroRepository(introRepositoryImpl: IntroRepositoryImpl): IntroRepository

  @Binds
  abstract fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

  @Binds
  abstract fun bindFeedbackRepository(feedbackRepositoryImpl: FeedbackRepositoryImpl): FeedbackRepository

  @Binds
  abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

  @Binds
  abstract fun bindHistoryRepository(historyRepositoryImpl: HistoryRepositoryImpl): HistoryRepository

  @Binds
  abstract fun bindRateRepository(rateRepositoryImpl: RateRepositoryImpl): RateRepository

  @Binds
  abstract fun bindDatabaseRepository(databaseRepositoryImpl: DatabaseRepositoryImpl): DatabaseRepository
}
