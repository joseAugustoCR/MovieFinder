package com.example.daggersample.di

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.daggersample.networking.NetworkEvent
import com.example.daggersample.networking.NetworkStatus
import com.example.moviefinder.BuildConfig
import com.example.moviefinder.R
import com.example.moviefinder.networking.Api
import com.example.moviefinder.ui.movies.MoviesDataSource
import com.example.moviefinder.ui.movies.MoviesDataSourceFactory
import com.example.moviefinder.utils.Constants
import com.example.moviefinder.utils.hasNetwork
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @Module
    companion object {


        @Singleton
        @Provides
        @JvmStatic
        fun provideUnauthorizedInterceptor(application:Application) : Interceptor {
            return object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request = chain.request()
                    var url = request.url()
                    if(url.queryParameter("api_key").isNullOrEmpty()){
                        val urlBuilder = url.newBuilder().addQueryParameter("api_key", Constants.API_KEY)
                        url = urlBuilder.build()
                    }
                    request = request.newBuilder().url(url).build()
                    val originalResponse = chain.proceed(request)
                    if (originalResponse.code() == 401) {
                        NetworkEvent.publish(NetworkStatus.UNAUTHORIZED)
                    }
                    return originalResponse
                }

            }
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideHttpClient(application: Application, unauthorizedInterceptor: Interceptor) : OkHttpClient{
            var RETROFIT_LOG_LEVEL = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(RETROFIT_LOG_LEVEL)

            val cacheSize = (Constants.CACHE_SIZE).toLong()
            val myCache =  Cache(application.applicationContext.cacheDir, cacheSize)

            val okHttpClient = OkHttpClient.Builder()
                .cache(myCache)
                    // Create the cache manager interceptor
                .addInterceptor{ chain ->

                    // Get the request from the chain.
                    var request = chain.request()

                    /*
                    *  Leveraging the advantage of using Kotlin,
                    *  we initialize the request and change its header depending on whether
                    *  the device is connected to Internet or not.
                    */
                    request = if (application.applicationContext.hasNetwork()!!)
                    /*
                    *  If there is Internet, get the cache that was stored X seconds ago.
                    *  If the cache is older than X seconds, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-age' attribute is responsible for this behavior.
                    */
                        request.newBuilder().header("Cache-Control", "public, max-age=" + Constants.MAX_AGE).build()
                    else
                    /*
                    *  If there is no Internet, get the cache that was stored 7 days ago.
                    *  If the cache is older than 7 days, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-stale' attribute is responsible for this behavior.
                    *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                    */
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + Constants.MAX_STALE).build()
                    // End of if-else statement

                    // Add the modified request to the chain.
                    chain.proceed(request)
                }
                .addInterceptor(unauthorizedInterceptor)
                .connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS).readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS).writeTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .build()

            return okHttpClient
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideRetrofitInstance(okHttpClient:OkHttpClient) : Retrofit {
            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        }


        @Singleton
        @JvmStatic
        @Provides
        fun provideApi(retrofit: Retrofit): Api {
            return retrofit.create(Api::class.java)
        }

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



    }

}