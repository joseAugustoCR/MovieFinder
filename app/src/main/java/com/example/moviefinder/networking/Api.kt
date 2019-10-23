package com.example.moviefinder.networking

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {


    @GET("discover/movie")
    fun discoverMovies(@Query("page") page:Int): Flowable<MoviesResponse>

}