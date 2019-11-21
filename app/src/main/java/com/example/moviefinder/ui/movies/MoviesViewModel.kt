package com.example.moviefinder.ui.movies

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviefinder.api.*
import com.example.moviefinder.repository.movies.MoviesDataSourceFactory
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.Executor


class MoviesViewModel @Inject constructor(var api:Api, val moviesDataFactory: MoviesDataSourceFactory, val networkExecutor: Executor): ViewModel() {
    var listLiveData:LiveData<PagedList<Movie>>?=null
    val networkState = Transformations.switchMap(moviesDataFactory.getDataSourceLiveData()){
        it.networkState
    }
    val initialLoad= Transformations.switchMap(moviesDataFactory.getDataSourceLiveData()){
        it.initialLoad
    }
    val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setInitialLoadSizeHint(30)
        .setPageSize(20)
        .build()


    fun getMovies() : LiveData<PagedList<Movie>>{
        if(listLiveData == null) {
            listLiveData = LivePagedListBuilder(moviesDataFactory, pagedListConfig)
                .setFetchExecutor(networkExecutor)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<Movie>(){
                    override fun onZeroItemsLoaded() {
                        super.onZeroItemsLoaded()
                    }
                })
                .build()
        }
        return listLiveData!!
    }

    fun performSearch(queryString: String?){
        moviesDataFactory.search(queryString)
    }

    fun rxJavaSample(){
        var subscription = api.getMovieDetails2("")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

}
