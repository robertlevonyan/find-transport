package robert.findtransport.di

import org.koin.dsl.module
import robert.findtransport.data.repository.*
import robert.findtransport.domain.repository.*

val repositoryModule = module {

  single<TransportsRepository> { TransportsRepositoryImpl(get(), get(), get(), get()) }

  single<StopsRepository> { StopsRepositoryImpl(get(), get(), get(), get()) }

  single<VersionRepository> { VersionRepositoryImpl(get(), get()) }

  single<ThemeRepository> { ThemeRepositoryImpl(get()) }

  single<LocaleRepository> { LocaleRepositoryImpl(get()) }

  single<ResourcesRepository> { ResourcesRepositoryImpl(get()) }

  single<IntroRepository> { IntroRepositoryImpl(get()) }

  single<SettingsRepository> { SettingsRepositoryImpl(get(), get(), get(), get()) }

  single<FeedbackRepository> { FeedbackRepositoryImpl(get(), get()) }

  single<LocationRepository> { LocationRepositoryImpl(get(), get()) }

  single<HistoryRepository> { HistoryRepositoryImpl(get()) }

  single<RateRepository> { RateRepositoryImpl(get()) }

  single<DatabaseRepository> { DatabaseRepositoryImpl(get(), get()) }

}
