package com.example.app.di.fragments

import androidx.lifecycle.ViewModel
import com.example.daggersample.di.ViewModelKey
import com.example.app.ui.main.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TimelineModule {
    @Binds
    @IntoMap
    @ViewModelKey(TimelineViewModel::class)
    abstract fun bindViewModel(viewModel: TimelineViewModel) : ViewModel


    @Module
    companion object{

    }
}