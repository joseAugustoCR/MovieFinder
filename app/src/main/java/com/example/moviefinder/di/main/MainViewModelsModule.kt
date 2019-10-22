package com.example.daggersample.di.main

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.moviefinder.ui.MainViewModel
import com.example.moviefinder.ui.movies.MoviesViewModel
import com.example.moviefinder.ui.moviedetails.MovieDetailsViewModel
import com.example.moviefinder.ui.tvshows.TVShowsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindProfileViewModel(viewModel: MainViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoviesViewModel::class)
    abstract fun bindDiscoverViewModel(viewModel: MoviesViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailsViewModel::class)
    abstract fun bindMovieDetailsViewModel(viewModel: MovieDetailsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TVShowsViewModel::class)
    abstract fun bindTVShowsViewModel(viewModel: TVShowsViewModel) : ViewModel

}