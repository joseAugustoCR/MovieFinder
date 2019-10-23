package com.example.moviefinder.ui.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviefinder.networking.Api
import com.example.moviefinder.networking.Movie
import com.example.moviefinder.networking.MoviesResponse
import com.example.moviefinder.networking.Resource
import com.github.ajalt.timberkt.d
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MoviesViewModel @Inject constructor(var api:Api, val moviesDataFactory: MoviesDataSourceFactory): ViewModel() {
    var response: MediatorLiveData<Resource<MoviesResponse>>? = null
    var listLiveData:LiveData<PagedList<Movie>>?=null

    init {
        d{"init"}
    }


    fun getMovies(){
        if(listLiveData == null) {
            val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(30)
                .setPageSize(20)
                .build()

            listLiveData = LivePagedListBuilder(moviesDataFactory, pagedListConfig)
                .setInitialLoadKey(1)
//                .setFetchExecutor(Executor { command -> command.run() })
                .build()
        }
    }

    fun observeDiscover() : LiveData<Resource<MoviesResponse>> {
        if(response == null){
            response = MediatorLiveData()
            response?.value = Resource.loading(null)

            var result = api.discoverMovies(2)
                .subscribeOn(Schedulers.io())
                .map (Function<MoviesResponse, Resource<MoviesResponse>>{
                        t : MoviesResponse ->
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
