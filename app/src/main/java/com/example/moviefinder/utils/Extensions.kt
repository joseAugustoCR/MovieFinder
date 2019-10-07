package com.example.moviefinder.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException

fun Context.hasNetwork(): Boolean? {
    var isConnected: Boolean? = false // Initial Value
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}

fun Throwable.getErrorMsg() : String{
    var statusCode: Int? = 0
    var errorMsg:String? = ""

    if (this is HttpException) {
        val responseBody: ResponseBody =
            (this as HttpException).response().errorBody() as ResponseBody
        try {
            val jsonObject = JSONObject(responseBody.string())
            errorMsg = jsonObject.getString("message")
        } catch (e: Exception) {
        }
    }
    statusCode = (this as HttpException).response()?.code()
    if(errorMsg.isNullOrEmpty() == false){
        return errorMsg.toString()
    }
    if(statusCode == 406 || statusCode == 409){
        return "User already exist"
    }
    return "Something went wrong"
}


fun Throwable.getStatusCode() : Int{
    var statusCode: Int? = 0
    statusCode = (this as HttpException).response()?.code()
    return if(statusCode != null) statusCode else 0
}


@SuppressLint("CheckResult")
fun ImageView.load(
    url: String,
    crop: Boolean = false,
    fade:Boolean = false,
    loadOnlyFromCache:Boolean = false,
    blur:Boolean = false,
    placeholder: Boolean = true,
    onLoadingFinished: () ->Unit={}

) {

    val listener = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
            onLoadingFinished()
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onLoadingFinished()
            return false
        }
    }


    Glide
        .with(context)
        .load(url)
        .onlyRetrieveFromCache(loadOnlyFromCache)
        .listener(listener)
        .thumbnail(0.15f)
        .let {
            if(placeholder){
//                it.placeholder(R.drawable.img_placeholder_programs)
            }
            if(crop){
                it.transform(CenterCrop())
            }else{
                it.transform(FitCenter())
            }
            if(blur){
                var multi  = MultiTransformation(CenterCrop(), BlurTransformation(20,2))
                it.transform(multi)
            }

            it
        }
        .let{
            if(fade){
                it.transition(DrawableTransitionOptions.withCrossFade(300))
            }else{
                it.dontTransform()
            }
            it
        }
        .into(this)
}

