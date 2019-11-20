package com.example.moviefinder.di.fragments

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.daggersample.di.main.FeatureModule
import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.ui.moviedetails.MovieDetailsViewModel
import com.example.moviefinder.ui.movies.MoviesAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = arrayOf(FeatureModule::class))
abstract class MovieDetailsModule {
    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailsViewModel::class)
    abstract fun bindMovieDetailsViewModel(viewModel: MovieDetailsViewModel) : ViewModel


    @Module
    companion object {


    }

}