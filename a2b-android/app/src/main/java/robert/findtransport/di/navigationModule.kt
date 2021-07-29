package robert.findtransport.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import org.koin.dsl.module

val navigationModule = module {
  single { Cicerone.create() }

  single {
    get<Cicerone<Router>>().router
  }
  single {
    get<Cicerone<Router>>().getNavigatorHolder()
  }
}
