package com.example.moviefinder.di.fragments

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.daggersample.di.main.FeatureModule
import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.api.Api
import com.example.moviefinder.repository.movies.MoviesDataSource
import com.example.moviefinder.repository.movies.MoviesDataSourceFactory
import com.example.moviefinder.ui.movies.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import java.util.concurrent.Executor
import javax.inject.Provider

@Module(includes = arrayOf(FeatureModule::class))
abstract class MoviesModule {

    @Binds
    @IntoMap
    @ViewModelKey(MoviesViewModel::class)
    abstract fun bindDiscoverViewModel(viewModel: MoviesViewModel) : ViewModel


    @Module
    companion object {
        @PerFragment
        @Provides
        @JvmStatic
        fun provideAdapter(fragment: MoviesFragment) : MoviesAdapter {
            return MoviesAdapter(fragment)
        }


        // unscoped: we need a new instance every time
        @JvmStatic
        @Provides
        fun provideMoviesDataSource(api: Api, retryExecutor: Executor) : MoviesDataSource {
            return MoviesDataSource(api, retryExecutor)
        }

        @PerFragment
        @JvmStatic
        @Provides
        fun provideMoviesDataSourceFactory(api:Api, executor: Executor, dataSourceProvider: Provider<MoviesDataSource>) : MoviesDataSourceFactory {
            return MoviesDataSourceFactory(
                dataSourceProvider
            )
        }


    }

}