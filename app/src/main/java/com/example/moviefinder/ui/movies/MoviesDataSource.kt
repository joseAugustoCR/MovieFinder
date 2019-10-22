package com.example.moviefinder.ui.movies

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.paging.PageKeyedDataSource
import com.example.moviefinder.networking.Api
import com.example.moviefinder.networking.DiscoverMovie
import com.example.moviefinder.networking.DiscoverMovieResponse
import com.example.moviefinder.networking.Resource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MoviesDataSource @Inject constructor(var api: Api) : PageKeyedDataSource<Int, DiscoverMovie>(){
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, DiscoverMovie>
    ) {
        var result = api.discoverMovies(2)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                callback.onResult(it.results!!, null, it?.total_results!!)

            }
            .doOnError {

            }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DiscoverMovie>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DiscoverMovie>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}