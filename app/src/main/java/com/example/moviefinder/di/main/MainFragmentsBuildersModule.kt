package com.example.daggersample.di


import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.di.fragments.DiscoverModule
import com.example.moviefinder.di.fragments.MovieDetailsModule
import com.example.moviefinder.ui.movies.MoviesFragment
import com.example.moviefinder.ui.moviedetails.MovieDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class MainFragmentsBuildersModule {

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(DiscoverModule::class)
    )
    abstract fun contributeDiscoverFragment(): MoviesFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(MovieDetailsModule::class)
    )
    abstract fun contributeMovieDetailsFragment(): MovieDetailsFragment
}