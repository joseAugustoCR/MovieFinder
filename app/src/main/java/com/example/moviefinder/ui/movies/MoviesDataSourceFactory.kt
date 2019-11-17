package com.example.moviefinder.ui.movies

import androidx.paging.DataSource
import androidx.lifecycle.MutableLiveData
import com.example.moviefinder.api.Movie
import com.github.ajalt.timberkt.d
import javax.inject.Inject
import javax.inject.Provider


class MoviesDataSourceFactory @Inject constructor(var dataSource: Provider<MoviesDataSource>): DataSource.Factory<Int, Movie>() {
    val dataSourceLiveData: MutableLiveData<MoviesDataSource> = MutableLiveData()
    var query:MutableLiveData<String> = MutableLiveData()

     // we need to provide a new instance of data source in order to call invalidate()
    override fun create(): DataSource<Int, Movie> {
        d{"recreating data source"}
        val dataSource= dataSource.get()
        dataSource.query = query.value
        dataSourceLiveData.postValue(dataSource)
        return dataSource
    }




}