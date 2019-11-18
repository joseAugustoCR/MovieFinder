package com.example.moviefinder.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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

    fun getMovieDetails(movieID:Int) : LiveData<Resource<Movie>>{
        var result = MediatorLiveData<Resource<Movie>>()
        result.value = Resource.loading()

        var data = api.getMovieDetails(movieID.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { Resource.success(it)  }
            .onErrorReturn {  Resource.error(it) }

        var source = data.toLiveData()!!//ReactiveStreams extension
        result?.addSource(source, {
            result?.value = it
            result?.removeSource(source)
        })
        return result
    }

}