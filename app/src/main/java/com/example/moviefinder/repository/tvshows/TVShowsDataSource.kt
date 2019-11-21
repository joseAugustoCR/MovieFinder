package com.example.moviefinder.repository.tvshows

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.moviefinder.api.*
import com.github.ajalt.timberkt.d
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import javax.inject.Inject

class TVShowsDataSource @Inject constructor(val api: Api, val retryExecutor:Executor) : PageKeyedDataSource<Int, TVShow>(){
    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    var query:String? = null
    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, TVShow>
    ) {
        d{"load initial " + this.toString()}

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            var response = api.let {
                if(query.isNullOrEmpty()){
                    it.discoverTVShows(1)
                }else{
                    it.searchTVShows(1, query.toString())
                }
            }.execute()

            if(response.isSuccessful) {
                var data = response.body()
                val items = data?.results ?: emptyList()
                retry = null
                networkState.postValue(NetworkState.LOADED)
                if(items.isEmpty()){
                    initialLoad.postValue(NetworkState.EMPTY)
                }else {
                    initialLoad.postValue(NetworkState.LOADED)
                }
                callback.onResult(items, null, 2)
            }else{
                retry = {
                    loadInitial(params, callback)
                }
                val error = NetworkState.error("error code ${response.code()}")
                networkState.postValue(error)
                initialLoad.postValue(error)
            }
        } catch (t: Exception) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(t.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }



    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, TVShow>) {
        networkState.postValue(NetworkState.LOADING)

        api.
        let {
            if(query.isNullOrEmpty()){
                it.discoverTVShows(params.key)
            }else{
                it.searchTVShows(params.key, query.toString())
            }
        }
            .enqueue(
            object : Callback<WrapperPagedApiResponse<TVShow>>{
                override fun onFailure(call: Call<WrapperPagedApiResponse<TVShow>>, t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    val error = NetworkState.error(t.message ?: "unknown error")
                    networkState.postValue(error)
                }

                override fun onResponse(
                    call: Call<WrapperPagedApiResponse<TVShow>>,
                    response: Response<WrapperPagedApiResponse<TVShow>>
                ) {
                    if(response.isSuccessful) {
                        val data = response.body()
                        var nextPage: Int? = null
                        if (data?.page!! < data.total_pages!!) {
                            nextPage = data.page!! + 1
                        }
                        val items = data.results ?: emptyList()
                        callback.onResult(items, nextPage)
                        networkState.postValue(NetworkState.LOADED)
                    }else{
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(
                            NetworkState.error("error code: ${response.code()}"))
                    }
                }

            }
        )
    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, TVShow>) {
        // ignored, since we only ever append to our initial load
    }



}