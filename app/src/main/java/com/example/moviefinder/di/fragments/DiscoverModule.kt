package com.example.moviefinder.di.fragments

import android.app.Application
import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.ui.discover.DiscoverMoviesAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DiscoverModule {

    @Module
    companion object {

        @PerFragment
        @Provides
        @JvmStatic
        fun provideAdapter(application: Application) : DiscoverMoviesAdapter {
            return DiscoverMoviesAdapter()
        }



    }

}