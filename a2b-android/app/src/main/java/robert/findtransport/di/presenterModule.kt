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

  viewModel { SplashViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }

  viewModel { IntroViewModel(get(), get()) }

  viewModel { HomeViewModel(get(), get(), get(), get(), get()) }

  viewModel { SearchViewModel(get(), get(), get(), get()) }

  viewModel { StopsPickerViewModel(get(), get()) }

  viewModel { DetailViewModel(get(), get()) }

  viewModel { MapViewModel(get(), get(), get()) }

  viewModel { StopOptionsViewModel(get(), get()) }

  viewModel { HistoryViewModel(get(), get()) }

  viewModel { FeedbackViewModel(get()) }

  viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }

  viewModel { LanguagePickerViewModel(get()) }

  viewModel { ThemePickerViewModel(get()) }

  viewModel { PassingRoutesViewModel(get(), get(), get()) }

  viewModel { UpdateViewModel(get(), get(), get()) }

  viewModel { TransportsViewModel(get(), get()) }

  viewModel { SearchMapViewModel(get(), get(), get()) }

  viewModel { TrackRouteViewModel() }
}
