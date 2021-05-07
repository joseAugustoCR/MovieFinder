package com.example.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.app.api.*
import com.example.app.utils.PASSWORD
import com.example.app.utils.SharedPreferencesManager
import com.example.app.utils.extensions.getError
import com.example.app.utils.extensions.toLiveData
import com.github.ajalt.timberkt.d
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val api: Api, private val sharedPreferencesManager: SharedPreferencesManager){


    fun getUser() : LiveData<Resource<User>> {
        val data = MediatorLiveData<Resource<User>>()
        data.value = Resource.loading()

        val source = api.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Resource.success(it)
            }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }

    fun connectTV(key:String, uid:String) : LiveData<Resource<TVConnectionResponse>> {
        val data = MediatorLiveData<Resource<TVConnectionResponse>>()
        data.value = Resource.loading()

        val source = api.connect(key, uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Resource.success(it)
            }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }

    fun getTerms(url:String) : LiveData<Resource<Response<ResponseBody>>> {
        val data = MediatorLiveData<Resource<Response<ResponseBody>>>()
        data.value = Resource.loading()

        val source = api.getTerms(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Resource.success(it)
            }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }

    fun acceptTerms() : LiveData<Resource<AcceptTermsResponse>> {
        val data = MediatorLiveData<Resource<AcceptTermsResponse>>()
        data.value = Resource.loading()

        val source = api.acceptTerms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Resource.success(it)
            }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }

    fun getConfigs() : LiveData<Resource<Configs>> {
        val data = MediatorLiveData<Resource<Configs>>()
        data.value = Resource.loading()

        val source = api.getConfigs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Resource.success(it)
            }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }


    fun getNotifications() : LiveData<Resource<ArrayList<Notification>>> {
        val data = MediatorLiveData<Resource<ArrayList<Notification>>>()
        data.value = Resource.loading()

        val source = api.getNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Resource.success(it)
            }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }

    fun createUser(request:RegisterRequest) : LiveData<Resource<User>> {
        val data = MediatorLiveData<Resource<User>>()
        data.value = Resource.loading()

        val source = api.createUser(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { Resource.success(it)  }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }


    fun login(request:LoginRequest) : LiveData<Resource<User>> {
        val data = MediatorLiveData<Resource<User>>()
        data.value = Resource.loading()

        val source = api.login(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
               sharedPreferencesManager.putObject(PASSWORD, request.password)
                Resource.success(it)
            }
            .onErrorReturn {  it.getError() }
            .toLiveData()

        data.addSource(source, {
            data.value = it
            data.removeSource(source)
        })
        d{"data value ${data.value.toString()}"}
        return data
    }
//
//    fun loginFacebook(request:FacebookLoginRequest) : LiveData<Resource<User>> {
//        val data = MediatorLiveData<Resource<User>>()
//        data.value = Resource.loading()
//
//        val source = api.loginFacebook(request)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .map { Resource.success(it)  }
//            .onErrorReturn {  it.getError() }
//            .toLiveData()
//
//        data.addSource(source, {
//            data.value = it
//            data.removeSource(source)
//        })
//        d{"data value ${data.value.toString()}"}
//        return data
//    }




}