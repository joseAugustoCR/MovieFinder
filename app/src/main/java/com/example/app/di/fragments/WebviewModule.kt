package com.example.app.di.fragments

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.webview.WebviewViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WebviewModule {
    @Binds
    @IntoMap
    @ViewModelKey(WebviewViewModel::class)
    abstract fun bindViewModel(viewModel: WebviewViewModel) : ViewModel


    @Module
    companion object{

    }
}