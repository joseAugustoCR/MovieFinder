package com.example.moviefinder.ui.movies

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviefinder.api.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.Executor


class MoviesViewModel @Inject constructor(var api:Api, val moviesDataFactory: MoviesDataSourceFactory, val networkExecutor: Executor): ViewModel() {
    var listLiveData:LiveData<PagedList<Movie>>?=null
    var networkState:LiveData<NetworkState>?= null
    var initialLoad:LiveData<NetworkState>?= null
    var query:LiveData<String> = MutableLiveData()
    //var Samirah:
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

            query = moviesDataFactory.query
            listLiveData = LivePagedListBuilder(moviesDataFactory, pagedListConfig)
                .setFetchExecutor(networkExecutor)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<Movie>(){
                    override fun onZeroItemsLoaded() {
                        super.onZeroItemsLoaded()
                    }
                })
                .build()
        }
    }

    fun search(query:String?){
        moviesDataFactory.query.value = query
        listLiveData?.value?.dataSource?.invalidate()
    }


    override fun onCleared() {
//        query.removeObserver(observerQuery)
        super.onCleared()


    }



    fun rxJavaSample(){
        var subscription = api.getMovieDetails2("")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

}
