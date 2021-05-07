package com.example.app.ui.main.notifications

import androidx.lifecycle.*
import com.example.app.api.Notification
import com.example.app.api.Post
import com.example.app.api.Resource
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.repository.UserRepository
import com.example.app.ui.main.home.HomeViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class NotificationsViewModel @AssistedInject constructor(private val userRepository: UserRepository, @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<NotificationsViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): NotificationsViewModel  // may be ommited prior kotlin 1.3.60 or after PR #121 in AssistedInject lib
    }

    private val requestNotifications = MutableLiveData<Boolean>()
    private var responseNotifications : LiveData<Resource<ArrayList<Notification>>> = Transformations.switchMap(requestNotifications){
        userRepository.getNotifications()
    }

    fun getNotifications(){
        requestNotifications.value = if(requestNotifications.value != true) false else true
    }

    fun observeNotifications() : LiveData<Resource<ArrayList<Notification>>> {
        return responseNotifications
    }
}