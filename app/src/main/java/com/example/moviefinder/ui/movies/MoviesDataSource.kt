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
import javax.inject.Inject

class MoviesDataSource @Inject constructor(val remoteDataSource: RemoteDataSource, val api: Api) : PageKeyedDataSource<Int, Movie>(){
    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()


    init {
        d{this.toString()}

    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        d{"load initial " + this.toString()}

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        var result2 = api.discoverMovies(2)
            .subscribeOn(Schedulers.io())
            .map (Function<MoviesResponse, Resource<MoviesResponse>>{
                    t : MoviesResponse ->
                Resource.success(t)
            }
            )
            .onErrorReturn {
                Resource.error(it, null)
            }

        val source = LiveDataReactiveStreams.fromPublisher(result2)

        var response = MediatorLiveData<Resource<MoviesResponse>>()
        response.addSource(source, {
            callback.onResult(it.data?.results!!, null, 1)
            response.removeSource(source)
        })
//        var result = api.discoverMovies(1)
//            .subscribeOn(Schedulers.io())
//            .subscribeWith(object : ResourceSubscriber<MoviesResponse>() {
//                override fun onComplete() {
//                }
//
//                override fun onNext(t: MoviesResponse?) {
//                    d{"loaded initial"}
//                    callback.onResult(t?.results!!, 0, t?.total_results!!, null, t?.page?.plus(1))
//                    networkState.postValue(NetworkState.LOADED)
//                    initialLoad.postValue(NetworkState.LOADED)
//                }
//
//                override fun onError(e: Throwable) {
//                    d{"error"}
//                    val error = NetworkState.error(e.message ?: "unknown error")
//                    networkState.postValue(error)
//                    initialLoad.postValue(error)
//                }
//
//            })



    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)

        var result = api.discoverMovies(params.key)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object :ResourceSubscriber<MoviesResponse>(){
                override fun onComplete() {

                }

                override fun onNext(t: MoviesResponse?) {
                    d{"loaded after"}
                    var nextPage:Int? = null
                    if(t?.page!! < t?.total_pages!!){
                        nextPage = t?.page!! +1
                    }
                    callback.onResult(t.results!!,  nextPage)
                    networkState.postValue(NetworkState.LOADED)

                }

                override fun onError(t: Throwable?) {
                    val error = NetworkState.error(t?.message ?: "unknown error")
                    networkState.postValue(error)
                }

            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)


        var result = api.discoverMovies(params.key)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object :ResourceSubscriber<MoviesResponse>(){
                override fun onComplete() {

                }

                override fun onNext(t: MoviesResponse?) {
                    d{"loaded before"}
                    var previousPage:Int? = null
                    if(t?.page!! > 1){
                        previousPage = t?.page!! -1
                    }
                    callback.onResult(t?.results!!,  previousPage)
                    networkState.postValue(NetworkState.LOADED)

                }

                override fun onError(t: Throwable?) {
                    d{"error"}
                    val error = NetworkState.error(t?.message ?: "unknown error")
                    networkState.postValue(error)
                }

            })

    }
}