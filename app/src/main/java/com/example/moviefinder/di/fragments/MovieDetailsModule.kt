package com.example.moviefinder.di.fragments

import android.app.Application
import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.ui.movies.MoviesAdapter
import dagger.Module
import dagger.Provides

@Module
class MovieDetailsModule {

    @Module
    companion object {

        @PerFragment
        @Provides
        @JvmStatic
        fun provideAdapter(application: Application) : MoviesAdapter {
            return MoviesAdapter()
        }



    }

}