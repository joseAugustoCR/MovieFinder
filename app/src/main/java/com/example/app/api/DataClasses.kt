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
    var terms_accepted:Boolean?=null,
    var api_token:String?=null,
    var tags:ArrayList<TagItem>?=null,
    var password:String?=null
) : Parcelable

data class AcceptTermsResponse(var status:String?=null)


data class TVConnectionResponse(
    var con: Boolean? = null,
    var `file`: String? = null,
    var ipmobi: String? = null,
    var iptv: String? = null,
    var sta: String? = null,
    var yid: String? = null
)

@Parcelize
data class TagItem(
    var key:String?=null,
    var value: String?=null
) : Parcelable

data class LoginRequest(
    var email:String?=null,
    var password:String?=null,
    var type:String?="APP"
)

data class RegisterRequest(
    var name:String?=null,
    var email:String?=null,
    var image:String?=null,
    var phonenumber:String?=null,
    var terms_accepted:Boolean?=null,
    var password:String?=null
)

@Parcelize
data class Configs(
    var apple_buy: Boolean? = null,
    var cast_url: String? = null,
    var conclusion_buy: String? = null,
    var contact: String? = null,
    var faq: String? = null,
    var faq_tv: String? = null,
    var idapplesignature: String? = null,
    var privacy: String? = null,
    var privacy_content: String? = null,
    var register: Boolean? = null,
    var terms: String? = null,
    var terms_content: String? = null,
    var update_terms_at: String? = null,
    var url_signature: String? = null,
    var urlplayer: String? = null,
    var whatsapp: String? = null,
    var whatsapp_msg: String? = null
) : Parcelable

data class Notification(
    var appuser_id: Int? = null,
    var created_at: String? = null,
    var url: String? = null,
    var id: Int? = null,
    var message: String? = null,
    var read_at: Any? = null,
    var title: String? = null,
    var type: String? = null
)

data class PostsViewRequest(
    var posts:ArrayList<Int>?= ArrayList()
)


data class ErrorResponse(
    var errorMsg: String?=null

    )


@Parcelize
data class Product(
    var id:Int?=null,
    var product_name:String?=null,
    var thumb:String?=null,
    var description:String?=null,
    var short_desc:String?=null,
    var free:Boolean?=null,
    var can_access:Boolean?=null,
) : Parcelable

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

data class Home(
    var productsByCategory: ArrayList<ProductsByCategory>?=null
)

data class ProductsByCategory(
    var id:Int?=null,
    var name: String?=null,
    var short_desc:String?=null,
    var products:ArrayList<Product>?=null
)