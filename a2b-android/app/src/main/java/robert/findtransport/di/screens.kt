package robert.findtransport.di

import android.os.Bundle
import com.github.terrakok.cicerone.androidx.FragmentScreen
import robert.findtransport.presentation.detail.DetailFragment
import robert.findtransport.presentation.feedback.FeedbackFragment
import robert.findtransport.presentation.history.HistoryFragment
import robert.findtransport.presentation.home.HomeFragment
import robert.findtransport.presentation.intro.IntroFragment
import robert.findtransport.presentation.map.ChooserMapFragment
import robert.findtransport.presentation.map.PreviewMapFragment
import robert.findtransport.presentation.map.SearchMapFragment
import robert.findtransport.presentation.passing.PassingRoutesFragment
import robert.findtransport.presentation.search.SearchFragment
import robert.findtransport.presentation.settings.SettingsFragment
import robert.findtransport.presentation.splash.SplashFragment
import robert.findtransport.presentation.stop.StopsPickerFragment
import robert.findtransport.presentation.track.TrackRouteFragment
import robert.findtransport.presentation.transports.TransportsFragment
import robert.findtransport.presentation.update.UpdateFragment

fun splashScreen() = FragmentScreen {
  SplashFragment.newInstance()
}

fun introScreen() = FragmentScreen {
  IntroFragment.newInstance()
}

fun homeScreen() = FragmentScreen {
  HomeFragment.newInstance()
}

fun stopsPickerScreen(type: Int) = FragmentScreen {
  StopsPickerFragment.newInstance(type)
}

fun searchScreen(args: Bundle) = FragmentScreen {
  SearchFragment.newInstance(args)
}

fun trackRouteScreen(args: Bundle) = FragmentScreen {
  TrackRouteFragment.newInstance(args)
}

fun transportsScreen() = FragmentScreen {
  TransportsFragment.newInstance()
}

fun detailsScreen(id: Int, hasOption: Boolean) = FragmentScreen {
  DetailFragment.newInstance(id, hasOption)
}

fun mapChooserScreen() = FragmentScreen {
  ChooserMapFragment.newInstance()
}

fun mapSearchScreen(args: Bundle) = FragmentScreen {
  SearchMapFragment.newInstance(args)
}

fun mapPreviewScreen(args: Bundle) = FragmentScreen {
  PreviewMapFragment.newInstance(args)
}

fun passingRoutesScreen(selectedStopId: Int) = FragmentScreen {
  PassingRoutesFragment.newInstance(selectedStopId)
}

fun updateScreen() = FragmentScreen {
  UpdateFragment.newInstance()
}

fun historyScreen() = FragmentScreen {
  HistoryFragment.newInstance()
}

fun feedbackScreen() = FragmentScreen {
  FeedbackFragment.newInstance()
}

fun settingsScreen() = FragmentScreen {
  SettingsFragment.newInstance()
}
