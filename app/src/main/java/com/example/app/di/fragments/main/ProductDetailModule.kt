package com.example.app.di.fragments.main

import androidx.lifecycle.ViewModel
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.ui.main.home.HomeViewModel
import com.example.app.ui.main.product_detail.ProductDetailViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProductDetailModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(CreateAccountWizardViewModel::class)
//    abstract fun bindViewModel(viewModel: CreateAccountWizardViewModel) : ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(ProductDetailViewModel::class)
    abstract fun bindVMFactory(f: ProductDetailViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Module
    companion object{

    }
}