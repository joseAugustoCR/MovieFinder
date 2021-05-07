package com.example.app.ui.main.community.create_post

import androidx.lifecycle.*
import com.example.app.api.Post
import com.example.app.api.Resource
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.repository.CommunityRepository
import com.example.app.ui.main.community.post_detail.PostDetailViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class CreatePostViewModel @AssistedInject constructor(private val communityRepository: CommunityRepository,
                                                      @Assisted private val savedStateHandle: SavedStateHandle): ViewModel() {


    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<CreatePostViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): CreatePostViewModel  // may be ommited prior kotlin 1.3.60 or after PR #121 in AssistedInject lib
    }

    private val requestCreate = MutableLiveData<Post>()
    private var responseCreate : LiveData<Resource<Post>> = Transformations.switchMap(requestCreate){
        communityRepository.createPost(it)
    }

    fun create(request:Post){
        requestCreate.value = request
    }

    fun observeCreate() : LiveData<Resource<Post>> {
        return responseCreate
    }
}