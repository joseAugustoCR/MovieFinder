package com.example.app.di.fragments

import androidx.lifecycle.ViewModel
import com.example.app.ui.terms.TermsViewModel
import com.example.daggersample.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TermsModule {
    @Binds
    @IntoMap
    @ViewModelKey(TermsViewModel::class)
    abstract fun bindViewModel(viewModel: TermsViewModel) : ViewModel


    @Module
    companion object{

    }
}