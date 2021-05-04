package com.example.app.di.fragments

import androidx.lifecycle.ViewModel
import com.example.app.ui.auth.login.LoginViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LoginModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindViewModel(viewModel: LoginViewModel) : ViewModel


    @Module
    companion object{

    }
}