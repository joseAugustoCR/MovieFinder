package com.example.moviefinder.ui.movies

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.moviefinder.networking.*
import com.github.ajalt.timberkt.d
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MoviesDataSource @Inject constructor(val remoteDataSource: RemoteDataSource, val api: Api) : PageKeyedDataSource<Int, Movie>(){
    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()

    //As suggested in the Android docs, we are using the synchronous version of the retrofit API

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        d{"load initial " + this.toString()}

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        api.discoverMovies(1).enqueue(
            object :Callback<MoviesResponse>{
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                    //todo retry
                    val error = NetworkState.error(t.message ?: "unknown error")
                    networkState.postValue(error)
                    initialLoad.postValue(error)
                }

                override fun onResponse(
                    call: Call<MoviesResponse>,
                    response: Response<MoviesResponse>
                ) {
                    val data = response.body()
                    val items = data?.results ?: emptyList()
                    callback.onResult(items, null, 2)
                }
            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)

        api.discoverMovies(params.key).enqueue(
            object : Callback<MoviesResponse>{
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                    val error = NetworkState.error(t.message ?: "unknown error")
                    networkState.postValue(error)
                }

                override fun onResponse(
                    call: Call<MoviesResponse>,
                    response: Response<MoviesResponse>
                ) {
                    val data = response.body()
                    var nextPage:Int? = null
                    if(data?.page!! < data.total_pages!!){
                        nextPage = data.page!! +1
                    }
                    val items = data.results ?: emptyList()
                    callback.onResult(items,  nextPage)
                    networkState.postValue(NetworkState.LOADED)
                }

            }
        )
    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)

        api.discoverMovies(params.key).enqueue(
            object : Callback<MoviesResponse>{
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                    val error = NetworkState.error(t.message ?: "unknown error")
                    networkState.postValue(error)
                }

                override fun onResponse(
                    call: Call<MoviesResponse>,
                    response: Response<MoviesResponse>
                ) {
                    val data = response.body()
                    var previousPage:Int? = null
                    if(data?.page!! > 1){
                        previousPage = data.page!! -1
                    }
                    val items = data.results ?: emptyList()
                    callback.onResult(items,  previousPage)
                    networkState.postValue(NetworkState.LOADED)
                }
            }
        )

    }
}