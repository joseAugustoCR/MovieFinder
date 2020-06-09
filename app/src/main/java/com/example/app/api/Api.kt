package com.example.app.api

import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    //user
    @GET("users/{id}")
    fun getUser(@Path("id") id:String?): Flowable<User>

    @POST("users")
    fun createUser(@Body user:User): Flowable<User>

    @POST("users/sign_in")
    fun login(@Body user:User): Flowable<User>

    @PATCH("users")
    fun editUser(@Body request: RequestBody) : Flowable<User>


}