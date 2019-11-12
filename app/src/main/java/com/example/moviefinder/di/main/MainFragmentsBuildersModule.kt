package com.example.daggersample.di


import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.di.fragments.*
import com.example.moviefinder.ui.movies.MoviesFragment
import com.example.moviefinder.ui.moviedetails.MovieDetailsFragment
import com.example.moviefinder.ui.search.SearchFragment
import com.example.moviefinder.ui.searchresult.SearchResultFragment
import com.example.moviefinder.ui.tvshowdetails.TVShowDetailsFragment
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

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(SearchModule::class)
    )
    abstract fun contributeSearchFragment(): SearchFragment


    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(SearchResultModule::class)
    )
    abstract fun contributeSearchResultFragment(): SearchResultFragment

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(TVShowDetailsModule::class)
    )
    abstract fun contributeTVShowDetailFragment(): TVShowDetailsFragment


}