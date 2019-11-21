package com.example.moviefinder.repository.tvshows

import androidx.paging.DataSource
import androidx.lifecycle.MutableLiveData
import com.example.moviefinder.api.TVShow
import com.github.ajalt.timberkt.d
import javax.inject.Inject
import javax.inject.Provider


class TVShowsDataSourceFactory @Inject constructor(var dataSource: Provider<TVShowsDataSource>): DataSource.Factory<Int, TVShow>() {
    val dataSourceLiveData: MutableLiveData<TVShowsDataSource> = MutableLiveData()
    var query:MutableLiveData<String> = MutableLiveData()

     // we need to provide a new instance of data source in order to call invalidate()
    override fun create(): DataSource<Int, TVShow> {
        d{"recreating data source"}
        val dataSource= dataSource.get()
        dataSource.query = query.value
        dataSourceLiveData.postValue(dataSource)
        return dataSource
    }




}