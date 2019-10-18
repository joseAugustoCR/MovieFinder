package com.example.daggersample.di.main

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.moviefinder.ui.MainViewModel
import com.example.moviefinder.ui.discover.DiscoverViewModel
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
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun bindDiscoverViewModel(viewModel: DiscoverViewModel) : ViewModel


}