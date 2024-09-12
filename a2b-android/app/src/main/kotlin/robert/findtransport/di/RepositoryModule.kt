package robert.findtransport.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import robert.findtransport.data.repository.DatabaseRepositoryImpl
import robert.findtransport.data.repository.FeedbackRepositoryImpl
import robert.findtransport.data.repository.HistoryRepositoryImpl
import robert.findtransport.data.repository.IntroRepositoryImpl
import robert.findtransport.data.repository.LocaleRepositoryImpl
import robert.findtransport.data.repository.LocationRepositoryImpl
import robert.findtransport.data.repository.RateRepositoryImpl
import robert.findtransport.data.repository.ResourcesRepositoryImpl
import robert.findtransport.data.repository.StopsRepositoryImpl
import robert.findtransport.data.repository.ThemeRepositoryImpl
import robert.findtransport.data.repository.TransportsRepositoryImpl
import robert.findtransport.data.repository.VersionRepositoryImpl
import robert.findtransport.domain.repository.DatabaseRepository
import robert.findtransport.domain.repository.FeedbackRepository
import robert.findtransport.domain.repository.HistoryRepository
import robert.findtransport.domain.repository.IntroRepository
import robert.findtransport.domain.repository.LocaleRepository
import robert.findtransport.domain.repository.LocationRepository
import robert.findtransport.domain.repository.RateRepository
import robert.findtransport.domain.repository.ResourcesRepository
import robert.findtransport.domain.repository.StopsRepository
import robert.findtransport.domain.repository.ThemeRepository
import robert.findtransport.domain.repository.TransportsRepository
import robert.findtransport.domain.repository.VersionRepository

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
