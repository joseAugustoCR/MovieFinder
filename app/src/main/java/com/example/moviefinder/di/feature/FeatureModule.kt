package com.example.daggersample.di.main

import com.example.moviefinder.di.feature.Feature
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

//Module can be used in many other modules (components)
@Module
class FeatureModule {

    @Module
    companion object {

        //Provide something to be used later with custom scope
        @FeatureScope
        @JvmStatic
        @Provides
        fun provideFoo(): Feature {
            return Feature()
        }



    }
}