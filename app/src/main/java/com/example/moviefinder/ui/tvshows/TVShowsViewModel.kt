package com.example.moviefinder.ui.tvshows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviefinder.api.Api
import com.example.moviefinder.api.NetworkState
import com.example.moviefinder.api.TVShow
import com.example.moviefinder.repository.tvshows.TVShowsDataSourceFactory
import java.util.concurrent.Executor
import javax.inject.Inject

class TVShowsViewModel @Inject constructor(var api: Api, val tvShowsDataSourceFactory: TVShowsDataSourceFactory, val networkExecutor: Executor) : ViewModel() {
    var listLiveData: LiveData<PagedList<TVShow>>?=null
    var networkState: LiveData<NetworkState>?= null
    var initialLoad: LiveData<NetworkState>?= null
    var query: LiveData<String> = MutableLiveData()

    fun getTVShows(){
        if(listLiveData == null) {
            val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(30)
                .setPageSize(20)
                .build()

            networkState = Transformations.switchMap(tvShowsDataSourceFactory.dataSourceLiveData){
                it.networkState
            }

            initialLoad = Transformations.switchMap(tvShowsDataSourceFactory.dataSourceLiveData){
                it.initialLoad
            }

            query = tvShowsDataSourceFactory.query
            listLiveData = LivePagedListBuilder(tvShowsDataSourceFactory, pagedListConfig)
                .setFetchExecutor(networkExecutor)
                .build()
        }
    }

    fun search(query:String?){
        tvShowsDataSourceFactory.query.value = query
        listLiveData?.value?.dataSource?.invalidate()
    }
}
