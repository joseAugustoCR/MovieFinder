package com.example.app.ui.main.cast_tv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.app.SessionManager
import com.example.app.api.Resource
import com.example.app.api.TVConnectionResponse
import com.example.app.repository.UserRepository
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class CastTVViewModel @Inject constructor(userRepository: UserRepository, sessionManager: SessionManager): ViewModel() {

    private val requestConnect = MutableLiveData<String>()
    private var responseConnect : LiveData<Resource<TVConnectionResponse>> = Transformations.switchMap(requestConnect){
        userRepository.connectTV(it, sessionManager.getUserID().toString())
    }

    fun connect(key:String){
        requestConnect.value = key
    }

    fun observeConnect() : LiveData<Resource<TVConnectionResponse>> {
        return responseConnect
    }

}