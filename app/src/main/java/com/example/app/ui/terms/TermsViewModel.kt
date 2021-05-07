package com.example.app.ui.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.app.api.AcceptTermsResponse
import com.example.app.api.Resource
import com.example.app.repository.UserRepository
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class TermsViewModel  @Inject constructor(userRepository: UserRepository) : ViewModel() {


    private val requestTerms = MutableLiveData<String>()
    private var responseTerms : LiveData<Resource<Response<ResponseBody>>> = Transformations.switchMap(requestTerms){
        userRepository.getTerms(it)
    }

    fun getTerms(url:String){
        requestTerms.value = url
    }

    fun observeTerms() : LiveData<Resource<Response<ResponseBody>>> {
        return responseTerms
    }


    private val requestAccept = MutableLiveData<Boolean>()
    private var responseAccept : LiveData<Resource<AcceptTermsResponse>> = Transformations.switchMap(requestAccept){
        userRepository.acceptTerms()
    }

    fun accept(){
        requestAccept.value = if(requestAccept.value != true) true else false
    }

    fun observeAccept() : LiveData<Resource<AcceptTermsResponse>> {
        return responseAccept
    }
}