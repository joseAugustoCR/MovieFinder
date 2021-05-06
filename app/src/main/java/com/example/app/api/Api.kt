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

    //user
    @GET("users/{id}")
    fun getUser(@Path("id") id:String?): Flowable<User>

    @POST("users")
    fun createUser(@Body user:User): Flowable<User>

    @POST("users/sign_in")
    fun login(@Body user:User): Flowable<User>

    @PATCH("users")
    fun editUser(@Body request: RequestBody) : Flowable<User>




    // POSTS
    @GET("app/posts/timeline")
    fun getCommunityPosts(
                    @Query("limit") limit:String?= Constants.PAGINATION_SIZE.toString(),
                    @Query("offset") offset:String?=null
    ) : Flowable<ArrayList<Post?>>

    @Headers("Accept: application/json")
    @POST("app/posts/{id}/view")
    fun viewPost(@Path("id") id:String): Flowable<String>

//    @Headers("Accept: application/json")
//    @POST("app/viewposts")
//    fun viewPosts(@Header("Authorization") auth: String?, @Body request:PostsViewRequest): Single<String>

    @Headers("Accept: application/json")
    @POST("app/posts/{id}/like")
    fun likePost(@Path("id") id:String): Flowable<Post>

    @Headers("Accept: application/json")
    @POST("app/posts/{id}/sharecount")
    fun shareCount(@Path("id") id:String): Flowable<Response<ResponseBody>>

    @Headers("Accept: application/json")
    @POST("app/posts/{id}/report")
    fun reportPost(@Path("id") id:String): Flowable<Post>

    @Headers("Accept: application/json")
    @DELETE("app/posts/{id}")
    fun deletePost(@Path("id") id:String): Flowable<Post>

    @Headers("Accept: application/json")
    @DELETE("app/posts/{id}/like")
    fun unlikePost(@Path("id") id:String): Flowable<Any>

    @Headers("Accept: application/json")
    @GET("app/posts/{id}")
    fun postDetail(@Path("id") id:String): Flowable<Post>

    @Headers("Accept: application/json")
    @POST("app/posts/{id}/comment")
    fun comment(@Path("id") id:String, @Body request:PostComment): Flowable<PostComment>

    @Headers("Accept: application/json")
    @POST("app/posts")
    fun createPost(@Body request:Post): Flowable<Post>


}