package com.example.app.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.app.api.RegisterRequest
import com.example.app.api.Resource
import com.example.app.api.User
import com.example.app.repository.UserRepository
import javax.inject.Inject

class RegisterViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val registerRequestLiveData = MutableLiveData<RegisterRequest>()
    private val registerResponse = Transformations.switchMap(registerRequestLiveData){
        return@switchMap userRepository.createUser(it)
    }
    private val editRequestLiveData = MutableLiveData<User>()
//    private val editResponse = Transformations.switchMap(editRequestLiveData){
//        return@switchMap userRepository.editUser(it)
//    }
    fun register(request: RegisterRequest){
        registerRequestLiveData.value = request
    }

    fun observeRegister() : LiveData<Resource<User>>{
        return registerResponse
    }

    fun edit(request: User){
        editRequestLiveData.value = request
    }

//    fun observeEdit() : LiveData<Resource<User>>{
//        return editResponse
//    }


}
