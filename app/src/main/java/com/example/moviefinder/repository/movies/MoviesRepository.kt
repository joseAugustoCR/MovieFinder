package com.example.moviefinder.repository.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.moviefinder.api.Api
import com.example.moviefinder.api.Movie
import com.example.moviefinder.api.Resource
import com.example.moviefinder.utils.toLiveData
import com.github.ajalt.timberkt.d
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepository @Inject constructor(val api:Api){

    fun getMovieDetails(movieID:String) : LiveData<Resource<Movie>>{
        val data = MediatorLiveData<Resource<Movie>>()
        data.value = Resource.loading()

        val source = api.getMovieDetails(movieID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { Resource.success(it)  }
            .onErrorReturn {  Resource.error(it) }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }



}