package com.example.daggersample.di

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.app.utils.SharedPreferencesManager
import com.example.app.R
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

//@Module(includes = MyModule.class)
@Module
class AppModule {

    @Module
    companion object {

        @Singleton
        @Provides
        @JvmStatic
        fun provideContext(application: Application) : Context = application.applicationContext


        @Singleton
        @JvmStatic
        @Provides
        fun provideRequestOptions(): RequestOptions {
            return RequestOptions().placeholder(R.color.colorPrimary).error(R.color.colorPrimaryDark)
        }

        @Singleton
        @JvmStatic
        @Provides
        fun provideRequestManager(application: Application, requestOptions: RequestOptions): RequestManager {
            return Glide.with(application).setDefaultRequestOptions(requestOptions)
        }

        @Singleton
        @JvmStatic
        @Provides
        fun provideAppDrawable(application:Application) : Drawable{
            return ContextCompat.getDrawable(application.applicationContext, R.mipmap.ic_launcher)!!
        }

//        @Singleton
//        @JvmStatic
//        @Provides
//        fun provideNetworkExecutor() : Executor {
//            return Executors.newFixedThreadPool(5)
//        }

//        @Singleton
//        @JvmStatic
//        @Provides
//        fun provideMoviesRepository(api: Api) : MoviesRepository {
//            return MoviesRepository(api = api)
//        }


        @Singleton
        @JvmStatic
        @Provides
        fun provideSharedPreferencesManager(application: Application) : SharedPreferencesManager {
            return SharedPreferencesManager(application.applicationContext)
        }

    }

}