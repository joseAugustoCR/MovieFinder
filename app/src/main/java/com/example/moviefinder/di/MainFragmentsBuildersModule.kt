package com.example.daggersample.di


import com.example.moviefinder.di.fragments.DiscoverModule
import com.example.moviefinder.ui.discover.DiscoverFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class MainFragmentsBuildersModule {

    @ContributesAndroidInjector(
        modules = arrayOf(DiscoverModule::class)
    )
    abstract fun contributeDiscoverFragment(): DiscoverFragment
}