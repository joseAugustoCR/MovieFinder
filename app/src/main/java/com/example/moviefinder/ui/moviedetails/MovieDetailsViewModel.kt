package com.example.moviefinder.ui.moviedetails

import android.provider.MediaStore
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
    val moviesRepository: MoviesRepository
): ViewModel() {
    val movie: LiveData<Resource<Movie>> = moviesRepository.getMovieDetails(112.toString())
    private var mediatorMovie:MediatorLiveData<Resource<Movie>>? = MediatorLiveData<Resource<Movie>>()


    init {
//        mediatorMovie.addSource(moviesRepository.getMovieDetails(112.toString())){
//            mediatorMovie.value = it
//        }
    }

    fun getMovie(movieID:Int) : LiveData<Resource<Movie>>{
        if(mediatorMovie == null){
            mediatorMovie = MediatorLiveData()
            mediatorMovie!!.addSource(moviesRepository.getMovieDetails(112.toString())){
                mediatorMovie!!.value = it
            }
        }'
        return mediatorMovie!!
    }

    fun getMovie2(movieID: Int):LiveData<Resource<Movie>>?{
        var teste = Transformations.map(movie){
            it.data?.id
        }

        return null
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







    @AssistedInject.Factory
    interface ViewModelAssistedFactory<MovieDetailsViewModel>

}
