package com.example.daggersample.di


import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.di.fragments.MoviesModule
import com.example.moviefinder.di.fragments.MovieDetailsModule
import com.example.moviefinder.di.fragments.TVShowsModule
import com.example.moviefinder.ui.movies.MoviesFragment
import com.example.moviefinder.ui.moviedetails.MovieDetailsFragment
import com.example.moviefinder.ui.tvshows.TVShowsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class MainFragmentsBuildersModule {

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(MoviesModule::class)
    )
    abstract fun contributeDiscoverFragment(): MoviesFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(MovieDetailsModule::class)
    )
    abstract fun contributeMovieDetailsFragment(): MovieDetailsFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(TVShowsModule::class)
    )
    abstract fun contributeTVShowsFragment(): TVShowsFragment


}