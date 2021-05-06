package com.example.daggersample.di

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.app.R
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.utils.SharedPreferencesManager
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@AssistedModule
@Module(includes=[AssistedInject_BuilderModule::class])
abstract class BuilderModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(CreateAccountWizardViewModel::class)
//    abstract fun bindVMFactory(f: CreateAccountWizardViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
}