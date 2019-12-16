package com.example.daggersample.di.main

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindProfileViewModel(viewModel: MainViewModel) : ViewModel

    @Module
    companion object {

//        @PerActivity
//        @JvmStatic
//        @Provides
//        fun provideAdapter(): PostAdapter {
//            return PostAdapter()
//        }



    }
}