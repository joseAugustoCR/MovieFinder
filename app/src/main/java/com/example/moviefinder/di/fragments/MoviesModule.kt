package com.example.moviefinder.di.fragments

import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.api.Api
import com.example.moviefinder.ui.movies.MoviesFragment
import com.example.moviefinder.ui.movies.MoviesAdapter
import com.example.moviefinder.ui.movies.MoviesDataSource
import com.example.moviefinder.ui.movies.MoviesDataSourceFactory
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import javax.inject.Provider

@Module
class MoviesModule {

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
            return MoviesDataSourceFactory(dataSourceProvider)
        }


    }

}