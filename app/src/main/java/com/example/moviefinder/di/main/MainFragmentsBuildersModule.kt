package com.example.daggersample.di


import com.example.daggersample.di.main.FeatureScope
import com.example.daggersample.di.main.PerFragment
import com.example.moviefinder.di.fragments.SplashModule
import com.example.moviefinder.di.fragments.TimelineModule
import com.example.moviefinder.ui.main.BottomNavFragment
import com.example.moviefinder.ui.main.splash.SplashFragment
import com.example.moviefinder.ui.main.timeline.TimelineFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class MainFragmentsBuildersModule {

    @PerFragment
    @ContributesAndroidInjector(
        modules = arrayOf(SplashModule::class)
    )
    abstract fun contributeSplashFragment(): SplashFragment

    @PerFragment
    @ContributesAndroidInjector(
    )
    abstract fun contributeBottomNavFragment(): BottomNavFragment


    @PerFragment
    @ContributesAndroidInjector(
       modules = arrayOf(TimelineModule::class)
    )
    abstract fun contributeTimelineFragment(): TimelineFragment


}