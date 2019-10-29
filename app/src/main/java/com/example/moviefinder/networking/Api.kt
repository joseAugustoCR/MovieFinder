package com.example.moviefinder.networking

import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {


    @GET("discover/movie")
    fun discoverMovies(@Query("page") page:Int): Flowable<MoviesResponse>

    @GET("discover/movie")
    fun fetch(@Query("page") page:Int): Response<MoviesResponse>

    @GET("discover/movie")
    fun fetchi(@Query("page") page:Int): Flowable<Result<MoviesResponse>>

}