package com.example.moviefinder.networking

import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {


    @GET("discover/movie")
    fun discoverMovies(@Query("page") page:Int): Call<MoviesResponse>

    @GET("discover/tv")
    fun discoverTVShows(@Query("page") page:Int): Call<TVShowsResponse>

    @GET("search/movie")
    fun searchMovies(@Query("page") page:Int, @Query("query") query:String): Call<MoviesResponse>

    @GET("search/movie")
    fun searchTVShows(@Query("page") page:Int, @Query("query") query:String): Call<TVShowsResponse>

}