package com.example.moviefinder.di.fragments

import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.networking.Api
import com.example.moviefinder.ui.movies.MoviesAdapter
import com.example.moviefinder.ui.movies.MoviesDataSource
import com.example.moviefinder.ui.movies.MoviesDataSourceFactory
import com.example.moviefinder.ui.movies.MoviesFragment
import com.example.moviefinder.ui.tvshows.TVShowsAdapter
import com.example.moviefinder.ui.tvshows.TVShowsDataSource
import com.example.moviefinder.ui.tvshows.TVShowsDataSourceFactory
import com.example.moviefinder.ui.tvshows.TVShowsFragment
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import javax.inject.Provider

@Module
class TVShowsModule {
    @Module
    companion object {
        @PerFragment
        @Provides
        @JvmStatic
        fun provideAdapter(fragment: TVShowsFragment) : TVShowsAdapter {
            return TVShowsAdapter(fragment)
        }


        // unscoped: we need a new instance every time
        @JvmStatic
        @Provides
        fun provideDataSource(api: Api, retryExecutor: Executor) : TVShowsDataSource {
            return TVShowsDataSource(api, retryExecutor)
        }

        @PerFragment
        @JvmStatic
        @Provides
        fun provideDataSourceFactory(api: Api, executor: Executor, dataSourceProvider: Provider<TVShowsDataSource>) : TVShowsDataSourceFactory {
            return TVShowsDataSourceFactory(dataSourceProvider)
        }
    }
}