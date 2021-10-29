package robert.findtransport.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
  @Singleton
  @Provides
  fun getCicerone(): Cicerone<Router> = Cicerone.create()
}

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {
  @Provides
  fun getRouter(router: Cicerone<Router>): Router = router.router
}

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
  @Provides
  fun getNavigatorHolder(router: Cicerone<Router>): NavigatorHolder = router.getNavigatorHolder()
}
