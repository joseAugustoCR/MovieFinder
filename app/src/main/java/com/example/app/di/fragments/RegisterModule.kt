package com.example.app.di.fragments

import androidx.lifecycle.ViewModel
import com.example.app.ui.auth.login.LoginViewModel
import com.example.app.ui.auth.register.RegisterViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class RegisterModule {
    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindViewModel(viewModel: RegisterViewModel) : ViewModel


    @Module
    companion object{

    }
}