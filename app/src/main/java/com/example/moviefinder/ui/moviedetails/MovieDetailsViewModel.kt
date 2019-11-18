package com.example.moviefinder.ui.moviedetails

import androidx.lifecycle.*
import com.example.moviefinder.api.Api
import com.example.moviefinder.api.Movie
import com.example.moviefinder.api.Resource
import com.example.moviefinder.api.TVShow
import com.example.moviefinder.repository.MoviesRepository
import com.example.moviefinder.utils.toLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailsViewModel  @Inject constructor(
    val api:Api,
    val moviesRepository: MoviesRepository
): ViewModel() {
    var movie:MediatorLiveData<Resource<Movie>>? = MediatorLiveData()
    var teste:MediatorLiveData<Resource<TVShow>>? = MediatorLiveData()

    fun newObserveMovie(movieID: Int) :  LiveData<Resource<Movie>> {
        return moviesRepository.getMovieDetails(movieID)
    }

    fun observeMovie(movieID:Int) : LiveData<Resource<Movie>> {
        if(movie == null){
            movie = MediatorLiveData()
            movie?.value = Resource.loading()

            var result = api.getMovieDetails(movieID.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { Resource.success(it)  }
                .onErrorReturn {  Resource.error(it) }

            var source = result.toLiveData()!!//ReactiveStreams extension
            movie?.addSource(source, {
                movie?.value = it
                movie?.removeSource(source)
            })
        }
        return movie!!
    }

    fun observeTeste(): LiveData<Resource<TVShow>>{
        var liveData = MutableLiveData<TVShow>()
        teste?.addSource(liveData, {})
        return teste!!
    }
}
