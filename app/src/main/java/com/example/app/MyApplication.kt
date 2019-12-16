package com.example.app

import android.content.Context
import androidx.multidex.MultiDex
import com.example.daggersample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class MyApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<MyApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}