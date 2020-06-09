package com.example.app.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.app.api.Resource
import com.example.app.api.User
import com.example.app.repository.UserRepository
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val userRequestLogin = MutableLiveData<User>()
//    private val requestFacebookLogin = MutableLiveData<FacebookLoginRequest>()
    private var loginResponse : LiveData<Resource<User>> = Transformations.switchMap(userRequestLogin){
        userRepository.login(it)
    }
//    private var loginFacebookResponse : LiveData<Resource<User>> = Transformations.switchMap(requestFacebookLogin){
//        userRepository.loginFacebook(it)
//    }

    fun login(request:User){
        userRequestLogin.value = request
    }

    fun observeLogin() : LiveData<Resource<User>>{
        return loginResponse
    }

//    fun loginFacebook(request: FacebookLoginRequest){
//        requestFacebookLogin.value = request
//    }
//
//    fun observeFacebookLogin() : LiveData<Resource<User>>{
//        return loginFacebookResponse
//    }


}
