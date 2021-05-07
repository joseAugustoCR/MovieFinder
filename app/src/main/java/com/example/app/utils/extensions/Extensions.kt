package com.example.app.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.app.api.Resource
import com.example.app.utils.CustomTabHelper
import com.example.app.R
import com.example.app.api.ErrorResponse
import com.example.app.base.REQUEST_TAKE_PICTURE
import com.example.app.utils.Constants
import com.example.app.utils.GlideApp
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import jp.wasabeef.glide.transformations.BlurTransformation
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

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
            (this as HttpException).response()?.errorBody() as ResponseBody
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


fun <T>Throwable.getError() : Resource<T>{
    var statusCode: Int? = 0
    var errorMsg:String? = ""

    if (this is HttpException) {
        statusCode = (this as HttpException).response()?.code()
        if(statusCode == null) return Resource.error()

        try {
            if(statusCode >= 400 && statusCode <=500) {
                val responseBody: ResponseBody =
                    (this as HttpException).response()?.errorBody() as ResponseBody
                val jsonObject = JSONObject(responseBody.string())
                if(jsonObject.has("msg")) {
                    val errorMsg =  jsonObject.getString("msg")
                    return Resource.error(ErrorResponse(errorMsg = errorMsg), statusCode)
                }else{
                    return Resource.error(null, statusCode)
                }
            }else{
                return Resource.error(null, statusCode)
            }
        } catch (e: Exception) {
            return Resource.error(null, statusCode)
        }
    }
   return Resource.error()
}

fun Throwable.getJSONObject(){
    //check if is a server error
    if(this is HttpException && this.getStatusCode() >= 400 && this.getStatusCode() <=500){
        val errorBody = this.response()?.errorBody()

    }
}


fun Throwable.getStatusCode() : Int{
    var statusCode: Int? = 0
    statusCode = (this as HttpException).response()?.code()
    return if(statusCode != null) statusCode else 0
}

@SuppressLint("CheckResult")
fun ImageView.loadColor(
    color:Int
){
    if(context.isAvailable() == false) return
    Glide
        .with(context)
        .load(color)
        .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.load(
    url: String,
    scaleType: ImageView.ScaleType?=null,
    @DrawableRes placeholder: Int?=null,
    isRoundImage:Boolean = false
){
    Glide
        .with(context)
        .load(url)
        .thumbnail(0.2f)
        .let {
            when(scaleType){
                ImageView.ScaleType.CENTER_CROP -> it.transform(CenterCrop())
                ImageView.ScaleType.FIT_CENTER -> it.transform(FitCenter())
                else -> it.transform(CenterCrop())
            }

            if(placeholder != null){
                it.placeholder(placeholder)
            }

            if(isRoundImage){
                val multi  = MultiTransformation(CenterCrop(), CircleCrop())
                it.transform(multi)
            }
            it
        }
        .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.loadDrawable(
    @DrawableRes drawable: Int?=null,
    scaleType: ImageView.ScaleType?=null,
    isRoundImage:Boolean = false
){
    Glide
        .with(context)
        .load(drawable)
        .let {
            when(scaleType){
                ImageView.ScaleType.CENTER_CROP -> it.transform(CenterCrop())
                ImageView.ScaleType.FIT_CENTER -> it.transform(FitCenter())
                else -> it.transform(CenterCrop())
            }


            if(isRoundImage){
                val multi  = MultiTransformation(CenterCrop(), CircleCrop())
                it.transform(multi)
            }
            it
        }
        .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.loadFile(
    file: File?=null,
    scaleType: ImageView.ScaleType?=null,
    isRoundImage:Boolean = false
){
    Glide
        .with(context)
        .load(file)
        .let {
            when(scaleType){
                ImageView.ScaleType.CENTER_CROP -> it.transform(CenterCrop())
                ImageView.ScaleType.FIT_CENTER -> it.transform(FitCenter())
                else -> it.transform(CenterCrop())
            }


            if(isRoundImage){
                val multi  = MultiTransformation(CenterCrop(), CircleCrop())
                it.transform(multi)
            }
            it
        }
        .into(this)
}

fun Context?.isAvailable(): Boolean {
    if (this == null) {
        return false
    } else if (this !is Application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (this is FragmentActivity) {
                return !this.isDestroyed
            } else if (this is Activity) {
                return !this.isDestroyed
            }
        }
    }
    return true
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow((findViewById(android.R.id.content) as View).getWindowToken(), 0);
}

fun Activity.showKeyboard(view:View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
}

fun <T> Flowable<T>.toLiveData() : LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(this)
}

fun <T> Observable<T>.toLiveData(backPressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER) :  LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(this.toFlowable(backPressureStrategy))
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> List<*>.checkItemsAre() =
    if (all { it is T })
        this as List<T>
    else null



fun Int.dp():Int{  return (this / Resources.getSystem().displayMetrics.density).toInt() }
fun Int.px():Int{  return (this * Resources.getSystem().displayMetrics.density).toInt() }


inline fun String.isValidEmail():Boolean{
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


fun TextInputEditText.afterTextChanged(inputLayout: TextInputLayout? = null, afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            if(inputLayout != null) {
                inputLayout.error = null
            }
        }
    })
}


inline fun String.loadOnBrowser(activity: Activity){
    try {
        val customTabHelper = CustomTabHelper()
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(activity as Context, R.color.colorPrimary))
        builder.setShowTitle(true)
        builder.setStartAnimations(activity as Context, android.R.anim.fade_in, android.R.anim.fade_out)
        builder.setExitAnimations(activity as Context, android.R.anim.fade_in, android.R.anim.fade_out)
        val packageName = customTabHelper.getPackageNameToUse(activity as Context, this)
        if (packageName == null){
            var i = Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(this));
            activity.startActivity(i);
        }
        else {
            var customTabsIntent = builder.build()
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(activity, Uri.parse(this))
        }
    } catch (e:Exception) {

    }
}



// snackbar
inline fun View.snack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(message: String, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snack.f()
    snack.show()
}


inline fun View.snack(message: String, color:Int, f: Snackbar.() -> Unit, iconResource:Int? = null) {
    var snack: Snackbar? = null
    if(iconResource != null){
        val builder = SpannableStringBuilder("  ");
        builder.setSpan(ImageSpan(this.context, iconResource), 0, builder.length-1, 0);
        builder.append(" $message");
        snack = Snackbar.make(this, builder, Snackbar.LENGTH_LONG)
    }else{
        snack = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    }
    snack.f()

    snack.getView().setBackgroundColor(ContextCompat.getColor(this.context, color))
    snack.show()
}


inline fun View.snack(message: String, color:Int, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    snack.f()
    snack.getView().setBackgroundColor(ContextCompat.getColor(this.context, color))
    snack.show()
}

fun String.luhnTest():Boolean{
    if (this.length < 13 || this.length > 16) {
        return false
    }
    var s1 = 0
    var s2 = 0
    val reverse = StringBuffer(this).reverse().toString()
    for (i in 0 until reverse.length) {
        val digit = Character.digit(reverse.get(i), 10)
        if (i % 2 == 0) {//this is for odd digits, they are 1-indexed in the algorithm
            s1 += digit
        } else {//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
            s2 += 2 * digit
            if (digit >= 5) {
                s2 -= 9
            }
        }
    }
    return (s1 + s2) % 10 == 0
}


fun Double.toCurrency() : String{
    val currencyFormat = NumberFormat.getCurrencyInstance()
    return currencyFormat.format(this)
}

fun String.toDateStringFormat(originalFormat:String, newFormat:String) : String{
    val originalformatter = SimpleDateFormat(originalFormat, Locale.getDefault())
    val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault())

    try {
        val date = originalformatter.parse(this)
        val dateTime = newFormatter.format(date)
        return dateTime

    } catch (e : java.lang.Exception) {
        e.printStackTrace();
    }

    return ""
}



fun String.toDate(originalFormat:String) : Date {
    val originalformatter = SimpleDateFormat(originalFormat)
    originalformatter.timeZone = TimeZone.getTimeZone("UTC")

    try {
        val date = originalformatter.parse(this)
        return date ?: Date()

    } catch (e : java.lang.Exception) {
        e.printStackTrace();
    }

    return Date()
}


inline fun String.loadOnExternalBrowser(activity: Activity){
    val uris = Uri.parse(this)
    val intents = Intent(Intent.ACTION_VIEW, uris)
    val b = Bundle()
    b.putBoolean("new_window", true)
    intents.putExtras(b)
    activity.startActivity(intents)
}

fun Context?.createTempImageFile(): File? {
    if(this == null){
        return  null
    }
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"

    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        this.cacheDir      /* directory */
    )

    return image
}


fun File.copyStreamToFile(inputStream: InputStream) {
    inputStream.use { input ->
        val outputStream = FileOutputStream(this)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
}


fun File.resizeImage(context: Context, maxWidth:Int = Constants.DEFAULT_IMG_MAX_WIDTH) : File? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(this.getAbsolutePath(), options)
    val imageHeight = options.outHeight
    val imageWidth = options.outWidth
    val scale = maxWidth.toFloat()/imageWidth.toFloat()

    val resizedFile = context.createTempImageFile()

    if (this.exists()) {

        val o = BitmapFactory.Options()
        var out: Bitmap? = null
        try {
            out = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.getAbsolutePath(), o), (imageWidth*scale).toInt(), (imageHeight*scale).toInt(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        } catch (e: OutOfMemoryError) {
            System.gc()
            o.inSampleSize = 2
            out = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.getAbsolutePath(), o), (imageWidth*scale).toInt(), (imageHeight*scale).toInt(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        }

        val matrix = Matrix()

        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(this.absolutePath)

            val orientation = exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> {
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        out = Bitmap.createBitmap(out!!, 0, 0, out.width, out.height,
            matrix, true)


        val fOut: FileOutputStream
        try {
            fOut = FileOutputStream(resizedFile)
            out!!.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            out.recycle()
        } catch (e: Exception) {
            if (out != null && !out.isRecycled) {
                out.recycle()
                out = null
            }
        }

    }

    return resizedFile
}









