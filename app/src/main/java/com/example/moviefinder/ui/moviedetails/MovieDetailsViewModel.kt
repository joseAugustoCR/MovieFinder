package com.example.moviefinder.ui.moviedetails

import androidx.lifecycle.*
import com.example.moviefinder.api.Api
import com.example.moviefinder.api.Movie
import com.example.moviefinder.api.Resource
import com.example.moviefinder.api.TVShow
import com.example.moviefinder.repository.MoviesRepository
import com.example.moviefinder.utils.toLiveData
import com.github.ajalt.timberkt.d
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieDetailsViewModel  @Inject constructor(
    val moviesRepository: MoviesRepository,
    @Assisted val handle:SavedStateHandle
): ViewModel() {
    val movie: LiveData<Resource<Movie>> = moviesRepository.getMovieDetails(112.toString())



//    fun observeMovie(movieID:Int) : LiveData<Resource<Movie>> {
//            movie.value = Resource.loading()
//            val result = moviesRepository.getMovieDetails(movieID)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map { Resource.success(it)  }
//                .onErrorReturn {  Resource.error(it) }
//
//            val source = result.toLiveData()//ReactiveStreams extension
//            movie.addSource(source, {
//                movie.value = it
//                movie.removeSource(source)
//            })
//        return movie
//    }







    @AssistedInject.Factory
    interface ViewModelAssistedFactory<MovieDetailsViewModel>

}
