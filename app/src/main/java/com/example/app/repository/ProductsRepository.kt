package com.example.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.app.SessionManager
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(private val api: Api, private val sessionManager: SessionManager){


    fun getHome() : LiveData<Resource<Home>> {
        val data = MediatorLiveData<Resource<Home>>()
        data.value = Resource.loading()

        val source = api
            .let {
                if(sessionManager.isLogged()){
                    it.getHomeAuth()
                }else{
                    it.getHome()
                }
            }
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





}