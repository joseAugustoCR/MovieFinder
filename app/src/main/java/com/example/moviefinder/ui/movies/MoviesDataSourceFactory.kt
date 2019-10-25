package com.example.moviefinder.ui.movies

import androidx.paging.DataSource
import androidx.lifecycle.MutableLiveData
import com.example.moviefinder.networking.Movie
import com.github.ajalt.timberkt.d
import javax.inject.Inject


class MoviesDataSourceFactory @Inject constructor(val dataSource: MoviesDataSource): DataSource.Factory<Int, Movie>() {
    val dataSourceLiveData: MutableLiveData<MoviesDataSource>? = MutableLiveData()

    init {
        d{this.toString()}
    }
    override fun create(): DataSource<Int, Movie> {
        d{this.toString()}
        dataSourceLiveData?.postValue(dataSource)
        return dataSource
    }


}