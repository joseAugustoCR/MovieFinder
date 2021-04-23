package com.example.app.di.fragments

import androidx.lifecycle.ViewModel
import com.example.app.ui.main.sale.SaleViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.webview.WebviewViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SaleModule {
    @Binds
    @IntoMap
    @ViewModelKey(SaleViewModel::class)
    abstract fun bindViewModel(viewModel: SaleViewModel) : ViewModel


    @Module
    companion object{

    }
}