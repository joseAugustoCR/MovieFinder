package com.example.daggersample.di

import com.example.daggersample.di.main.MainModule
import com.example.daggersample.di.main.PerActivity
import com.example.moviefinder.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class ActivityBuildersModule {

    @PerActivity
    @ContributesAndroidInjector(
        modules = arrayOf(MainModule::class, MainFragmentsBuildersModule::class)
    )
    abstract fun contributeMainActivity(): MainActivity


    // Kotlin doesn't have static methods so we need to wrap in a companion object and annotate with @JvmStatic
    @Module
    companion object {

//        @JvmStatic
//        @Provides //declare a string dependency
//        fun doSomething(): String {
//            return "Doing something..."
//        }


    }
}