package com.example.moviefinder.ui.movies

import androidx.paging.DataSource
import com.example.moviefinder.networking.DiscoverMovie
import androidx.lifecycle.MutableLiveData
import com.example.moviefinder.ui.movies.MoviesDataFactory
import com.example.moviefinder.networking.DiscoverMovie




class MoviesDataFactory : DataSource.Factory<Int, DiscoverMovie>() {
    private val dataSourceLiveData: MutableLiveData<DiscoverMovie>? = null
    private val  dataSource:MoviesDataSource?=null

    override fun create(): DataSource<Int, DiscoverMovie> {
        dataSourceLiveData?.postValue(dataSource)
        return dataSource

    }


}