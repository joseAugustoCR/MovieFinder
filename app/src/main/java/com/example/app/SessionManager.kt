package com.example.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app.utils.SharedPreferencesManager
import com.example.app.api.AuthResource
import com.example.app.api.User
import com.example.app.utils.AUTHORIZATION
import com.example.app.utils.LOGGED_USER
import com.example.app.utils.PASSWORD
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(var sharedPreferences: SharedPreferencesManager) {
    private var cachedUser:MutableLiveData<AuthResource<User>> = MutableLiveData()

    init {
        val user = sharedPreferences.getObject(LOGGED_USER, User::class.java)
        if(user == null){
            logout()
        }else{
            login(user)
        }
    }

    fun login(user:User?){
        if(user == null){
            logout()
            return
        }
        cachedUser.value = AuthResource.authenticated(user)
        sharedPreferences.putObject(LOGGED_USER, user)

    }

    fun updateUser(user:User?){
        if(user == null){
            logout()
            return
        }
        cachedUser.value = AuthResource.authenticated(user)
        sharedPreferences.putObject(LOGGED_USER, user)
    }

    fun testUnauth(){
        sharedPreferences.deleteObject(AUTHORIZATION)
    }


    fun logout(){
        cachedUser.postValue(AuthResource.logout())
        sharedPreferences.deleteObject(LOGGED_USER)
        sharedPreferences.deleteObject(AUTHORIZATION)
        sharedPreferences.deleteObject(PASSWORD)
    }

    fun getAuthUser() : LiveData<AuthResource<User>>{
        return cachedUser
    }

    fun isLogged() : Boolean{
        return cachedUser.value?.status == AuthResource.AuthStatus.AUTHENTICATED
    }

    fun getUser(): User?{
        return getAuthUser().value?.data
    }

    fun isSubscriber():Boolean{
        return cachedUser.value?.data?.subscribed == true
    }
    fun getUserID():Int?{
        return cachedUser.value?.data?.id
    }


}