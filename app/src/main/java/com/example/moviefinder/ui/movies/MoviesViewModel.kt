package com.example.moviefinder.ui.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.moviefinder.networking.Api
import com.example.moviefinder.networking.DiscoverMovieResponse
import com.example.moviefinder.networking.Resource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MoviesViewModel @Inject constructor(var api:Api): ViewModel() {
    var response: MediatorLiveData<Resource<DiscoverMovieResponse>>? = null


    fun observeDiscover() : LiveData<Resource<DiscoverMovieResponse>> {
        if(response == null){
            response = MediatorLiveData()
            response?.value = Resource.loading(null)

            var result = api.discoverMovies(2)
                .subscribeOn(Schedulers.io())
                .map (Function<DiscoverMovieResponse, Resource<DiscoverMovieResponse>>{
                        t : DiscoverMovieResponse ->
                    Resource.success(t)
                }
                )
                .onErrorReturn {
                    Resource.error(it, null)
                }

            val source = LiveDataReactiveStreams.fromPublisher(result)
            response?.addSource(source, {
                response?.value = it
                response?.removeSource(source)
            })
        }
        return response!!
    }
}
