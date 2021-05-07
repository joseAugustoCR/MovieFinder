package com.example.app.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.app.utils.extensions.toLiveData
import com.example.app.api.AuthResource
import com.example.app.api.User
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

val RECENT_SEARCHES = "recent_searches"
val AUTHORIZATION = "auth"
val LOGGED_USER = "logged_user"
val PASSWORD = "password"
val INTRO = "intro"
val ONESIGNAL_ID = "onesignal_id"
val VIEWED_POSTS = "viewed_posts"
val TERMS_ACCEPTED = "terms_accepted"
val CONFIGS = "configs"
val CAST_KEY = "cast_key"


@Singleton
class SharedPreferencesManager @Inject constructor(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val gson = GsonBuilder().create()

    /**
     * Saves object into the Preferences.
     **/
    fun <T> putObject(key: String, y: T) {
        val inString = gson.toJson(y)
        preferences.edit().putString(
            key,
            inString
        ).apply()
    }


    /**
     * Deletes object from the Preferences.
     **/
    fun deleteObject(key: String) {
        preferences.edit().remove(
            key
        ).apply()
    }

    /**
     * Returns objects into the Preferences.
     **/

    fun <T> getObject(key: String, c: Class<T>): T? {
        val value = preferences.getString(key, null)
        try {
            if (value != null) {
                //JSON String was found which means object can be read.
                //We convert this JSON String to model object. Parameter "c" (of type Class<T>" is used to cast.
                return gson.fromJson(value, c)
            }
        } catch (e: Exception) {
        }
        return null
    }


    fun getStringSet(key: String): Set<String>? {
        val value = preferences.getStringSet(key, null)
        return value
    }

    fun putStringSet(key:String, set:Set<String>){
        preferences.edit().putStringSet(
            key,
            set
        ).apply()
    }


    fun <T> putArrayList(key:String,  array:ArrayList<T>){
        val inString = gson.toJson(array)

    }


    //    returns an observable object
    fun <T> getObjectLiveData(key: String, c: Class<T>): LiveData<T?> {
        val myObject:T? = getObject(key, c)
        if(myObject != null) {
            return Observable.fromCallable {
                return@fromCallable myObject
            }.toLiveData()
        }
        return MutableLiveData<T>()
    }


    //    returns an observable object
    fun getUserLiveData(): LiveData<AuthResource<User>>? {
        val myObject = getObject("user", User::class.java)
        return Observable.fromCallable {
            return@fromCallable AuthResource.authenticated(myObject)
        }.toLiveData()
    }
}