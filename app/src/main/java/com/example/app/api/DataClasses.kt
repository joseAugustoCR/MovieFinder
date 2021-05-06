package com.example.app.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id:Int?=null,
    var name:String?=null,
    var image:String?=null,
    var verified:Boolean?=null,
    var subscribed:Boolean?=null,
    var password:String?=null
) : Parcelable


data class PostsViewRequest(
    var posts:ArrayList<Int>?= ArrayList()
)


data class ErrorResponse(var id:Int?=null)


data class Product(
    var id:Int?=null,
    var name:String?=null,
)

data class Category(
    var id:Int?=null,
    var title:String?=null,
    var subtitle:String?=null,
    var items:ArrayList<Product>?=null
)


@Parcelize
data class Post(
    var id:Int?=null,
    var appuser_id:Int?=null,
    var desc: String?=null,
    var type:String?=null,
    var image: String?=null,
    var file: String?=null,
    var id_youtube:String?=null,
    var url_share:String?=null,
    var hash:String?=null,
    var views_count:Int?=null,
    var likes_count:Int?=null,
    var shares_count:Int?=null,
    var comments_count:Int?=null,
    var call_to_action_title:String?=null,
    var call_to_action_link:String?=null,
    var ago:String?=null,
    var created_at:String?=null,
    var comments: ArrayList<PostComment?>?=null,
    var last_comment: PostComment?=null,
    var appuser: User?=null,
    var has_view:Boolean?=null,
    var has_like:Boolean?=null,
    var subscribe:Boolean?=null,
    var has_comment:Boolean?=null
): Parcelable{
    companion object{
        val TYPE_TEXT = "text"
        val TYPE_IMAGE = "image"
        val TYPE_YOUTUBE = "youtube"
        val TYPE_FILE = "file"
        val TYPE_LIVE = "live"
        val TYPE_LOADING = "loading"
    }
}


@Parcelize
data class PostComment(
    var id: Int?=null,
    var appuser: User?=null,
    var comment: String?=null,
    var ago: String?=null,
    var comment_reply_id:Int?=null,
    var reply:ArrayList<PostComment>?=null
) : Parcelable
