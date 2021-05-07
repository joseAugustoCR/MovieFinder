package com.example.app.di.fragments.main

import androidx.lifecycle.ViewModel
import com.example.app.ui.main.cast_tv.CastTVViewModel
import com.example.daggersample.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CastTVModule {
    @Binds
    @IntoMap
    @ViewModelKey(CastTVViewModel::class)
    abstract fun bindViewModel(viewModel: CastTVViewModel) : ViewModel


    @Module
    companion object{

    }
}