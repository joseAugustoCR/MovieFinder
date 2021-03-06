package com.example.daggersample.di

import android.app.Application
import android.content.Context
import android.os.Build
import com.example.moviefinder.MyApplication
import com.example.moviefinder.SessionManager
import com.example.moviefinder.api.User
import com.example.moviefinder.utils.SharedPreferencesManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

//Modules that has a singleton scope
@Singleton
@Component(
    modules = arrayOf(AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        AppModule::class,
        ViewModelFactoryModule::class
    )
)
interface AppComponent : AndroidInjector<MyApplication> {
    fun sessionManager(): SessionManager
    fun sharedPreferencesManager() : SharedPreferencesManager

    @Component.Builder
    interface Builder{
        @BindsInstance fun application(application: Application) : Builder
        fun build() : AppComponent
    }
    
}