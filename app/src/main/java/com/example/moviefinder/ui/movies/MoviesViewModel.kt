package com.example.moviefinder.ui.movies

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviefinder.networking.*
import com.github.ajalt.timberkt.d
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_movies.view.*
import javax.inject.Inject
import com.elifox.legocatalog.api.BaseDataSource
import kotlin.Result


class MoviesViewModel @Inject constructor(var api:Api, val moviesDataFactory: MoviesDataSourceFactory): ViewModel() {
    var response: MediatorLiveData<Resource<MoviesResponse>>? = null
    var response2: MediatorLiveData<com.example.moviefinder.networking.Result<MoviesResponse>>? = null
    var listLiveData:LiveData<PagedList<Movie>>?=null
    var networkState:LiveData<NetworkState>?= null
    var initialLoad:LiveData<NetworkState>?= null

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

            networkState = Transformations.switchMap(moviesDataFactory.dataSourceLiveData){
                it.networkState
            }

            initialLoad = Transformations.switchMap(moviesDataFactory.dataSourceLiveData){
                it.initialLoad
            }

            listLiveData = LivePagedListBuilder(moviesDataFactory, pagedListConfig)
                .build()
        }
    }

    fun test(){
        var result = api.fetchi(1)
            .subscribeOn(Schedulers.io())
        val source = LiveDataReactiveStreams.fromPublisher(result)

        response2?.addSource(source, {
            response2?.value = it
            response2?.removeSource(source)
        })

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
