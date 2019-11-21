package com.example.moviefinder.ui.moviedetails

import androidx.lifecycle.*
import com.example.moviefinder.api.Movie
import com.example.moviefinder.api.Resource
import com.example.moviefinder.repository.movies.MoviesRepository
import javax.inject.Inject

class MovieDetailsViewModel  @Inject constructor(
    private val moviesRepository: MoviesRepository
): ViewModel() {
    private val movieIDLiveData = MutableLiveData<Int>()
    private val movieResponse = Transformations.switchMap(movieIDLiveData){
        moviesRepository.getMovieDetails(it.toString())
    }

    fun fetchMovie(movieID:Int){
            movieIDLiveData.value = movieID
    }

    fun getMovie() : LiveData<Resource<Movie>>{
        return movieResponse
    }


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

}
