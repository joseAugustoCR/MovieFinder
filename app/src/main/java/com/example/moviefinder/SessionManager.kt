package com.example.moviefinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.moviefinder.api.AuthResource
import com.example.moviefinder.api.Resource
import com.example.moviefinder.api.User
import com.example.moviefinder.utils.SharedPreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(var sharedPreferences:SharedPreferencesManager) {
    private var cachedUser:MutableLiveData<AuthResource<User>> = MutableLiveData()

//    fun authenticateWithId(source : LiveData<AuthResource<User>>?){
//        if(source == null) return
//        cachedUser.value = AuthResource.loading(null)
//        cachedUser.addSource(source, Observer {
//            cachedUser.value = it
//            cachedUser.removeSource(source)
//        })
//    }

    init {
        //TODO retrive from sp
    }

    fun login(user:User?){
        if(user == null){
            logout()
            return
        }
        cachedUser.value = AuthResource.authenticated(user)
        sharedPreferences.putObject("user", user)

    }


    fun logout(){
        cachedUser.postValue(AuthResource.logout())
        sharedPreferences.deleteObject("user")
    }

    fun getAuthUser() : LiveData<AuthResource<User>>{
        return cachedUser
    }


}