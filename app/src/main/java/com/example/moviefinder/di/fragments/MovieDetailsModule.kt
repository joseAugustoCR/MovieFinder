package com.example.moviefinder.di.fragments

import android.app.Application
import com.example.daggersample.di.main.FeatureModule
import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.ui.movies.MoviesAdapter
import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(FeatureModule::class))
class MovieDetailsModule {

    @Module
    companion object {


    }

}