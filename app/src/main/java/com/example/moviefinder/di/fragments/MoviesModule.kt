package com.example.moviefinder.di.fragments

import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.networking.Api
import com.example.moviefinder.networking.RemoteDataSource
import com.example.moviefinder.ui.movies.MoviesFragment
import com.example.moviefinder.ui.movies.MoviesAdapter
import com.example.moviefinder.ui.movies.MoviesDataSource
import com.example.moviefinder.ui.movies.MoviesDataSourceFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

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

        @PerFragment
        @JvmStatic
        @Provides
        fun provideMoviesDataSource(remoteDataSource: RemoteDataSource, api: Api) : MoviesDataSource {
            return MoviesDataSource(remoteDataSource, api)
        }

        @PerFragment
        @JvmStatic
        @Provides
        fun provideMoviesDataSourceFactory(moviesDataSource: MoviesDataSource) : MoviesDataSourceFactory {
            return MoviesDataSourceFactory(moviesDataSource)
        }


    }

}