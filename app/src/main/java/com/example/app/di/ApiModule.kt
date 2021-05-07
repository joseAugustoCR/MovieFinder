package com.example.daggersample.di

import android.app.Application
import android.content.Context
import android.os.Build
import com.example.daggersample.networking.NetworkEvent
import com.example.app.utils.*
import com.example.app.utils.extensions.hasNetwork
import com.example.app.BuildConfig
import com.example.app.SessionManager
import com.example.app.api.Api
import com.example.app.api.NetworkStatus
import com.example.app.utils.Constants
import com.github.ajalt.timberkt.d
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

//@Module(includes = MyModule.class)
@Module
class ApiModule {

    @Module
    companion object {

        @Singleton
        @Provides
        @JvmStatic
        fun provideContext(application: Application) : Context = application.applicationContext

        @Singleton
        @Provides
        @JvmStatic
        @Named("unauthorizedInterceptor")
        fun provideUnauthorizedInterceptor(application:Application, sharedPreferencesManager: SharedPreferencesManager, sessionManager: SessionManager) : Interceptor {
            return object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request().newBuilder()
                    d{"unauth interceptor"}
                    //if user is logged, put the authorization header
                    val auth = sessionManager.getUserAuthorization()
                    if(auth.isNullOrEmpty() == false){
                        d{"auth " + auth}
                        request.addHeader("Authorization", auth)
                    }
                    // put the api key
                    var requestWithApiKey = request
                        .addHeader("os", "Android")
                        .addHeader("appVersion", BuildConfig.VERSION_CODE .toString())
                        .addHeader("osVersion", Build.VERSION.RELEASE .toString())
                        .addHeader("Accept", "application/json")
                        .build()



                    val originalResponse = chain.proceed(requestWithApiKey)
                    //check if it's not login or register
                    if(requestWithApiKey.url.toString().equals(Constants.BASE_URL + "users") == false){
                        d{"request not login related " + originalResponse.code}
                        if (originalResponse.code == 401) {
                            NetworkEvent.publish(NetworkStatus.UNAUTHORIZED)
                        }
                    }
                    return originalResponse
                }

            }
        }

//        @Singleton
//        @Provides
//        @JvmStatic
//        @Named("loginInterceptor")
//        fun provideLoginInterceptor(application:Application, sharedPreferencesManager: SharedPreferencesManager) : Interceptor {
//            return object : Interceptor{
//                override fun intercept(chain: Interceptor.Chain): Response {
//                    d{"login interceptor"}
//                    var request = chain.request()
//                    val originalResponse = chain.proceed(request)
//
//                    if(request.url.toString().contains("users/sign_in") || request.url.toString().contains("users/facebook_sign_in")
//                        || (request.url.toString().equals(Constants.BASE_URL + "users") && request.method.equals("POST") )){
//                        if(originalResponse.code == 200){
//                            val header = originalResponse.header("Authorization", null)
//                            if(header != null){
//                                d{"Auth register = $header"}
//                                sharedPreferencesManager.putObject(AUTHORIZATION, header)
//                            }
//                        }
//                    }
//                    return originalResponse
//                }
//            }
//        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideHttpClient(application: Application, @Named("unauthorizedInterceptor") unauthorizedInterceptor: Interceptor
        ) : OkHttpClient{
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
//                .addInterceptor (loginInterceptor)
                .addInterceptor (interceptor)
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
        fun provideNetworkExecutor() : Executor {
            return Executors.newFixedThreadPool(5)
        }





    }

}