package com.example.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.app.api.*
import com.example.app.repository.CommunityRepository
import com.example.app.repository.UserRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(var communityRepository: CommunityRepository,
                                        var userRepository: UserRepository
                                        ) : ViewModel() {
    //view
    private val requestView = MutableLiveData<PostsViewRequest>()
    private var responseView : LiveData<Resource<String>> = Transformations.switchMap(requestView){
        communityRepository.viewPosts(it)
    }

    fun view(request: PostsViewRequest){
        requestView.value = request
    }

    fun observeView() : LiveData<Resource<String>> {
        return responseView
    }

    // User
    private val idRequestLiveData = MutableLiveData<Int>()
    private val userResponse = Transformations.switchMap(idRequestLiveData){
        return@switchMap userRepository.getUser()
    }

    fun getUser(id:Int){
        idRequestLiveData.value = id
    }

    fun observeUser():LiveData<Resource<User>>{
        return userResponse
    }

    // Configs
    private val requestConfigs = MutableLiveData<Boolean>()
    private val responseConfigs = Transformations.switchMap(requestConfigs){
        return@switchMap userRepository.getConfigs()
    }

    fun getConfigs(){
        requestConfigs.value = if(requestConfigs.value != true) true else false
    }

    fun observeConfigs():LiveData<Resource<Configs>>{
        return responseConfigs
    }
}