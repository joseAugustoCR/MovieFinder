package com.example.app.di.fragments

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SplashModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindViewModel(viewModel: SplashViewModel) : ViewModel


    @Module
    companion object{

    }
}