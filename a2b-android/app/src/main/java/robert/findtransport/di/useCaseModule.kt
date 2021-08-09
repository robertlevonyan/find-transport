package robert.findtransport.di

import org.koin.dsl.module
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
import robert.findtransport.domain.usecase.settings.SettingsUseCase
import robert.findtransport.domain.usecase.settings.SettingsUseCaseImpl
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCaseImpl
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCaseImpl

val useCaseModule = module {
  single<CheckInternetUseCase> { CheckInternetUseCaseImpl(get()) }

  single<VersionUseCase> { VersionUseCaseImpl(get()) }

  single<DatabaseUseCase> { DatabaseUseCaseImpl(get()) }

  single<ThemeUseCase> { ThemeUseCaseImpl(get()) }

  single<LocaleUseCase> { LocaleUseCaseImpl(get()) }

  single<IntroUseCase> { IntroUseCaseImpl(get(), get()) }

  single<SettingsUseCase> { SettingsUseCaseImpl(get()) }

  single<FeedbackUseCase> { FeedbackUseCaseImpl(get()) }

  single<StopsUseCase> { StopsUseCaseImpl(get(), get(), get()) }

  single<TransportUseCase> { TransportUseCaseImpl(get(), get()) }

  single<PermissionUseCase> { PermissionUseCaseImpl(get()) }

  single<HistoryUseCase> { HistoryUseCaseImpl(get(), get()) }

  single<RateUseCase> { RateUseCaseImpl(get()) }

  single<LocationUseCase> { LocationUseCaseImpl(get()) }
}
