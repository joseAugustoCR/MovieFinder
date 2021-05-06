package com.example.daggersample.di

import android.app.Application
import com.example.app.MyApplication
import com.example.app.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

//Modules that has a singleton scope
@Singleton
@Component(
    modules = arrayOf(AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        AppModule::class,
        ViewModelFactoryModule::class,
        BuilderModule::class,
        ApiModule::class
    )
)
interface AppComponent : AndroidInjector<MyApplication> {
    fun sessionManager(): SessionManager

    @Component.Builder
    interface Builder{
        @BindsInstance fun application(application: Application) : Builder
        fun build() : AppComponent
    }
    
}