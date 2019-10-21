package com.example.moviefinder.di.fragments

import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.ui.movies.MoviesFragment
import com.example.moviefinder.ui.movies.MoviesAdapter
import dagger.Module
import dagger.Provides

@Module
class DiscoverModule {

    @Module
    companion object {

        @PerFragment
        @Provides
        @JvmStatic
        fun provideAdapter(fragment: MoviesFragment) : MoviesAdapter {
            return MoviesAdapter(fragment)
        }



    }

}