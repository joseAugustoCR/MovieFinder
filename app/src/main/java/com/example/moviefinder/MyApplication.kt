package com.example.moviefinder

import com.example.daggersample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MyApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<MyApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}