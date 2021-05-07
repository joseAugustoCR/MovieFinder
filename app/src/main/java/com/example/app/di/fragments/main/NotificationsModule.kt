package com.example.app.di.fragments.main

import androidx.lifecycle.ViewModel
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.ui.main.community.CommunityViewModel
import com.example.app.ui.main.home.HomeViewModel
import com.example.app.ui.main.notifications.NotificationsViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NotificationsModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(CreateAccountWizardViewModel::class)
//    abstract fun bindViewModel(viewModel: CreateAccountWizardViewModel) : ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun bindVMFactory(f: NotificationsViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Module
    companion object{

    }
}