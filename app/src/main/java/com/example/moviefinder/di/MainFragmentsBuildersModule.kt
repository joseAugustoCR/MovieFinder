package com.example.daggersample.di


import com.example.moviefinder.ui.discover.DiscoverFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class MainFragmentsBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeDiscoverFragment(): DiscoverFragment
}