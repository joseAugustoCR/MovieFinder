package com.example.moviefinder.api

import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {


    @GET("discover/movie")
    fun discoverMovies(@Query("page") page:Int): Call<WrapperPagedApiResponse<Movie>>

    @GET("discover/tv")
    fun discoverTVShows(@Query("page") page:Int): Call<WrapperPagedApiResponse<TVShow>>

    @GET("search/movie")
    fun searchMovies(@Query("page") page:Int, @Query("query") query:String): Call<WrapperPagedApiResponse<Movie>>

    @GET("search/movie")
    fun searchTVShows(@Query("page") page:Int, @Query("query") query:String): Call<WrapperPagedApiResponse<TVShow>>

    @GET("movie/{id}")
    fun getMovieDetails(@Path("id") movieID:String) : Flowable<Movie>

    @GET("discover/movie")
    fun discoverMovies2(@Query("page") page:Int): Flowable<WrapperPagedApiResponse<Movie>>

    @GET("movie/{id}")
    fun getMovieDetails2(@Path("id") movieID:String) : Observable<Movie>

}