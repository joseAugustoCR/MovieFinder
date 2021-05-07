package com.example.app.di.fragments.main.community

import androidx.lifecycle.ViewModel
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.ui.main.community.CommunityViewModel
import com.example.app.ui.main.community.create_post.CreatePostViewModel
import com.example.app.ui.main.home.HomeViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CreatePostModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(CreateAccountWizardViewModel::class)
//    abstract fun bindViewModel(viewModel: CreateAccountWizardViewModel) : ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(CreatePostViewModel::class)
    abstract fun bindVMFactory(f: CreatePostViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Module
    companion object{

    }
}