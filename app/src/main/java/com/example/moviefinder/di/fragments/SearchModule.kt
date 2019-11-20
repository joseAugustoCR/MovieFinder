package com.example.moviefinder.di.fragments

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.moviefinder.ui.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SearchModule {
    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel) : ViewModel


}