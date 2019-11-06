package com.example.moviefinder.ui.movies

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviefinder.networking.*
import com.github.ajalt.timberkt.d
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_movies.view.*
import javax.inject.Inject
import com.elifox.legocatalog.api.BaseDataSource
import java.util.concurrent.Executor
import kotlin.Result


class MoviesViewModel @Inject constructor(var api:Api, val moviesDataFactory: MoviesDataSourceFactory, val networkExecutor: Executor): ViewModel() {
    var listLiveData:LiveData<PagedList<Movie>>?=null
    var networkState:LiveData<NetworkState>?= null
    var initialLoad:LiveData<NetworkState>?= null
    var query:LiveData<String> = MutableLiveData()

    fun getMovies(){
        if(listLiveData == null) {
            val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(30)
                .setPageSize(20)
                .build()

            networkState = Transformations.switchMap(moviesDataFactory.dataSourceLiveData){
                it.networkState
            }

            initialLoad = Transformations.switchMap(moviesDataFactory.dataSourceLiveData){
                it.initialLoad
            }

            query = moviesDataFactory.query
            listLiveData = LivePagedListBuilder(moviesDataFactory, pagedListConfig)
                .setFetchExecutor(networkExecutor)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<Movie>(){
                    override fun onZeroItemsLoaded() {
                        super.onZeroItemsLoaded()
                    }
                })
                .build()
        }
    }

    fun search(query:String?){
        moviesDataFactory.query.value = query
        listLiveData?.value?.dataSource?.invalidate()
    }


    override fun onCleared() {
//        query.removeObserver(observerQuery)
        super.onCleared()
    }

//    fun observeDiscover() : LiveData<Resource<MoviesResponse>> {
//        if(response == null){
//            response = MediatorLiveData()
//            response?.value = Resource.loading(null)
//
//
//            var result = api.discoverMovies(2)
//                .subscribeOn(Schedulers.io())
//                .map (Function<MoviesResponse, Resource<MoviesResponse>>{
//                        t : MoviesResponse ->
//                    Resource.success(t)
//                }
//                )
//                .onErrorReturn {
//                    Resource.error(it, null)
//                }
//
//            val source = LiveDataReactiveStreams.fromPublisher(result)
//            response?.addSource(source, {
//                response?.value = it
//                response?.removeSource(source)
//            })
//        }
//        return response!!
//    }
}
