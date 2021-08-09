package robert.findtransport.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import robert.findtransport.presentation.component.bottomsheet.language.LanguagePickerViewModel
import robert.findtransport.presentation.component.bottomsheet.map.StopOptionsViewModel
import robert.findtransport.presentation.component.bottomsheet.theme.ThemePickerViewModel
import robert.findtransport.presentation.detail.DetailViewModel
import robert.findtransport.presentation.feedback.FeedbackViewModel
import robert.findtransport.presentation.history.HistoryViewModel
import robert.findtransport.presentation.home.HomeViewModel
import robert.findtransport.presentation.intro.IntroViewModel
import robert.findtransport.presentation.map.MapViewModel
import robert.findtransport.presentation.map.SearchMapViewModel
import robert.findtransport.presentation.passing.PassingRoutesViewModel
import robert.findtransport.presentation.search.SearchViewModel
import robert.findtransport.presentation.settings.SettingsViewModel
import robert.findtransport.presentation.splash.SplashViewModel
import robert.findtransport.presentation.stop.StopsPickerViewModel
import robert.findtransport.presentation.track.TrackRouteViewModel
import robert.findtransport.presentation.transports.TransportsViewModel
import robert.findtransport.presentation.update.UpdateViewModel

val presenterModule = module {
  viewModel {
    SplashViewModel(
      checkInternetUseCase = get(),
      themeUseCase = get(),
      localeUseCase = get(),
      versionUseCase = get(),
      introUseCase = get(),
      stopsUseCase = get(),
      transportUseCase = get(),
      databaseUseCase = get(),
      feedbackUseCase = get(),
    )
  }

  viewModel {
    IntroViewModel(
      introUseCase = get(),
      localeUseCase = get(),
    )
  }

  viewModel {
    HomeViewModel(
      localeUseCase = get(),
      stopsUseCase = get(),
      transportUseCase = get(),
      permissionUseCase = get(),
      rateUseCase = get(),
    )
  }

  viewModel {
    SearchViewModel(
      localeUseCase = get(),
      stopsUseCase = get(),
      transportUseCase = get(),
      historyUseCase = get(),
    )
  }

  viewModel {
    StopsPickerViewModel(
      stopsUseCase = get(),
      localeUseCase = get(),
    )
  }

  viewModel {
    DetailViewModel(
      localeUseCase = get(),
      transportUseCase = get(),
    )
  }

  viewModel {
    MapViewModel(
      stopsUseCase = get(),
      localeUseCase = get(),
      transportUseCase = get(),
      locationUseCase = get(),
    )
  }

  viewModel {
    StopOptionsViewModel(
      localeUseCase = get(),
      stopsUseCase = get(),
    )
  }

  viewModel {
    HistoryViewModel(
      localeUseCase = get(),
      historyUseCase = get(),
    )
  }

  viewModel { FeedbackViewModel(feedbackUseCase = get()) }

  viewModel {
    SettingsViewModel(
      settingsUseCase = get(),
      themeUseCase = get(),
      stopsUseCase = get(),
      transportUseCase = get(),
      databaseUseCase = get(),
    )
  }

  viewModel { LanguagePickerViewModel(settingsUseCase = get()) }

  viewModel { ThemePickerViewModel(settingsUseCase = get()) }

  viewModel {
    PassingRoutesViewModel(
      localeUseCase = get(),
      stopsUseCase = get(),
      transportUseCase = get(),
    )
  }

  viewModel {
    UpdateViewModel(
      databaseUseCase = get(),
      stopsUseCase = get(),
      transportUseCase = get(),
    )
  }

  viewModel {
    TransportsViewModel(
      localeUseCase = get(),
      transportUseCase = get(),
    )
  }

  viewModel {
    SearchMapViewModel(
      localeUseCase = get(),
      stopsUseCase = get(),
      transportUseCase = get(),
      locationUseCase = get(),
    )
  }

  viewModel { TrackRouteViewModel() }
}
