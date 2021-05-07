package com.example.app.api

import com.example.app.utils.Constants
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {

    // USER
    @GET("app/user")
    fun getUser() : Flowable<User>

    @POST("app/user")
    fun createUser(@Body request: RegisterRequest): Flowable<User>

    @POST("app/login")
    fun login(@Body user:LoginRequest): Flowable<User>

    @GET("app/home")
    fun getConfigs() : Flowable<Configs>

    @GET
    fun getTerms(@Url url:String?) : Flowable<Response<ResponseBody>>

    @POST("app/user/terms")
    fun acceptTerms() : Flowable<AcceptTermsResponse>


    // CAST
    @POST("cast/connect")
    fun connect (@Query("key") key:String, @Query("uid") uid:String?=null ) : Flowable<TVConnectionResponse>


    // HOME
    @GET("app/timeline")
    fun getHome() : Flowable<Home>

    @GET("app/timeline/auth")
    fun getHomeAuth() : Flowable<Home>



    // NOTIFICATIONS
    @GET("app/notifications")
    fun getNotifications() : Flowable<ArrayList<Notification>>



    // COMMUNITY
    @GET("app/posts/timeline")
    fun getCommunityPosts(
                    @Query("limit") limit:String?= Constants.PAGINATION_SIZE.toString(),
                    @Query("offset") offset:String?=null
    ) : Flowable<ArrayList<Post?>>

    @POST("app/posts/{id}/view")
    fun viewPost(@Path("id") id:String): Flowable<String>

    @POST("app/viewposts")
    fun viewPosts(@Body request:PostsViewRequest): Flowable<String>

    @POST("app/posts/{id}/like")
    fun likePost(@Path("id") id:String): Flowable<Post>

    @POST("app/posts/{id}/sharecount")
    fun shareCount(@Path("id") id:String): Flowable<Response<ResponseBody>>

    @POST("app/posts/{id}/report")
    fun reportPost(@Path("id") id:String): Flowable<Post>

    @DELETE("app/posts/{id}")
    fun deletePost(@Path("id") id:String): Flowable<Post>

    @DELETE("app/posts/{id}/like")
    fun unlikePost(@Path("id") id:String): Flowable<Any>

    @GET("app/posts/{id}")
    fun postDetail(@Path("id") id:String): Flowable<Post>

    @POST("app/posts/{id}/comment")
    fun comment(@Path("id") id:String, @Body request:PostComment): Flowable<PostComment>

    @POST("app/posts")
    fun createPost(@Body request:Post): Flowable<Post>


}