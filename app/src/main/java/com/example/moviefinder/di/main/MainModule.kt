package com.example.daggersample.di.main

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.moviefinder.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

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