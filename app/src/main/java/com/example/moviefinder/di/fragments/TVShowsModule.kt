package com.example.moviefinder.di.fragments

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.api.Api
import com.example.moviefinder.repository.tvshows.TVShowsDataSource
import com.example.moviefinder.repository.tvshows.TVShowsDataSourceFactory
import com.example.moviefinder.ui.tvshows.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import java.util.concurrent.Executor
import javax.inject.Provider

@Module
abstract class TVShowsModule {

    @Binds
    @IntoMap
    @ViewModelKey(TVShowsViewModel::class)
    abstract fun bindTVShowsViewModel(viewModel: TVShowsViewModel) : ViewModel


    @Module
    companion object {
        @PerFragment
        @Provides
        @JvmStatic
        fun provideAdapter(fragment: TVShowsFragment) : TVShowsAdapter {
            return TVShowsAdapter(fragment)
        }


        // unscoped: we need a new instance every time
        @JvmStatic
        @Provides
        fun provideDataSource(api: Api, retryExecutor: Executor) : TVShowsDataSource {
            return TVShowsDataSource(api, retryExecutor)
        }

        @PerFragment
        @JvmStatic
        @Provides
        fun provideDataSourceFactory(api: Api, executor: Executor, dataSourceProvider: Provider<TVShowsDataSource>) : TVShowsDataSourceFactory {
            return TVShowsDataSourceFactory(
                dataSourceProvider
            )
        }
    }
}