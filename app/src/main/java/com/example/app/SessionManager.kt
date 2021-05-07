package com.example.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app.api.AuthResource
import com.example.app.api.Configs
import com.example.app.api.User
import com.example.app.utils.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(var sharedPreferences: SharedPreferencesManager) {
    private var cachedUser:MutableLiveData<AuthResource<User>> = MutableLiveData()
    private var configs:MutableLiveData<Configs> = MutableLiveData()
    private var castKey:MutableLiveData<String> = MutableLiveData()


    init {
        val user = sharedPreferences.getObject(LOGGED_USER, User::class.java)
        if(user == null){
            logout()
        }else{
            login(user)
        }

        val configsData = sharedPreferences.getObject(CONFIGS, Configs::class.java)
        setConfigs(configsData)

        val castKey = sharedPreferences.getObject(CAST_KEY, String::class.java)
        setCastKey(castKey)

    }

    fun login(user:User?){
        if(user == null){
            logout()
            return
        }
        cachedUser.value = AuthResource.authenticated(user)
        sharedPreferences.putObject(LOGGED_USER, user)

    }

    fun setConfigs(data:Configs?){
        configs.value = data
        sharedPreferences.putObject(CONFIGS, data)

    }

    fun setCastKey(data:String?){
        castKey.value = data
        sharedPreferences.putObject(CAST_KEY, data)
    }

    fun updateUser(user:User?){
        if(user == null){
            logout()
            return
        }
        cachedUser.value = AuthResource.authenticated(user)
        sharedPreferences.putObject(LOGGED_USER, user)
    }

    fun getUserAuthorization():String?{
        return "Bearer ${getUser()?.api_token}"
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

    fun acceptTerms():Boolean{
        return cachedUser.value?.data?.terms_accepted != true && configs.value?.terms.isNullOrEmpty() == false
    }

    fun getUserID():Int?{
        return cachedUser.value?.data?.id
    }

    fun getConfigs():Configs?{
        return configs.value
    }

    fun setTermsAccepted(){
        val user = getUser()
        user?.terms_accepted = true
        login(user)
    }

    fun getCastKey():String?{
        return castKey.value
    }


}