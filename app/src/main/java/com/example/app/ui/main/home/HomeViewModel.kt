package com.example.app.ui.main.home

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import com.example.app.api.Home
import com.example.app.api.Notification
import com.example.app.api.Resource
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.repository.ProductsRepository
import com.example.app.repository.UserRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(private val productsRepository: ProductsRepository, @Assisted private val savedStateHandle: SavedStateHandle): ViewModel() {
    var scrollStates: MutableMap<Int, Parcelable?> = mutableMapOf()
    set(value) {
        field = value
        savedStateHandle.set(STATE_KEY_SCROLL_STATES, value)
    }

    var recyclerStateBundle:Bundle= bundleOf()
    set(value) {
        field = value
        savedStateHandle.set(STATE_KEY_SCROLL_STATE_BUNDLE, value)
    }

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<HomeViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): HomeViewModel  // may be ommited prior kotlin 1.3.60 or after PR #121 in AssistedInject lib
    }

    val STATE_KEY_SCROLL_STATES = "state.scroll_state"
    val STATE_KEY_SCROLL_STATE_BUNDLE = "state.scroll_state_bundle"

    init {
        scrollStates = savedStateHandle.get(STATE_KEY_SCROLL_STATES) ?: mutableMapOf()
        recyclerStateBundle = savedStateHandle.get(STATE_KEY_SCROLL_STATE_BUNDLE) ?: bundleOf()
    }


    // Home
    private val requestHome = MutableLiveData<Boolean>()
    private var responseHome : LiveData<Resource<Home>> = Transformations.switchMap(requestHome){
        productsRepository.getHome()
    }

    fun getHome(){
        requestHome.value = if(requestHome.value != true) false else true
    }

    fun observeHome() : LiveData<Resource<Home>> {
        return responseHome
    }

}