package com.example.app.ui.main.community.post_detail

import androidx.lifecycle.*
import com.example.app.api.Post
import com.example.app.api.PostComment
import com.example.app.api.Resource
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.repository.CommunityRepository
import com.example.app.ui.main.community.CommunityViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import okhttp3.ResponseBody
import retrofit2.Response

class PostDetailViewModel @AssistedInject constructor(private val communityRepository: CommunityRepository,
                                                      @Assisted private val savedStateHandle: SavedStateHandle): ViewModel() {

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PostDetailViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): PostDetailViewModel  // may be ommited prior kotlin 1.3.60 or after PR #121 in AssistedInject lib
    }

    //postDetail
    private val requestDetail = MutableLiveData<Int>()
    private var responseDetail : LiveData<Resource<Post>> = Transformations.switchMap(requestDetail){
        communityRepository.getPostDetail(it)
    }

    fun getPostDetail(id:Int){
        requestDetail.value = id
    }

    fun observeDetail() : LiveData<Resource<Post>> {
        return responseDetail
    }

    //post comment
    private val requestComment = MutableLiveData<PostComment>()
    private var responseComment : LiveData<Resource<PostComment>> = Transformations.switchMap(requestComment){
        communityRepository.postComment(it)
    }

    fun comment(request:PostComment){
        requestComment.value = request
    }

    fun observeComment() : LiveData<Resource<PostComment>> {
        return responseComment
    }

    //like
    private val requestLike = MutableLiveData<Int>()
    private var responseLike : LiveData<Resource<Post>> = Transformations.switchMap(requestLike){
        communityRepository.like(it)
    }

    fun like(id:Int){
        requestLike.value = id
    }

    fun observLike() : LiveData<Resource<Post>> {
        return responseLike
    }


    //unlike
    private val requestUnlike = MutableLiveData<Int>()
    private var responseUnlike : LiveData<Resource<Any>> = Transformations.switchMap(requestUnlike){
        communityRepository.unlike(it)
    }

    fun unlike(id:Int){
        requestUnlike.value = id
    }

    fun observUnlike() : LiveData<Resource<Any>> {
        return responseUnlike
    }

    //view
    private val requestView = MutableLiveData<Int>()
    private var responseView : LiveData<Resource<String>> = Transformations.switchMap(requestView){
        communityRepository.viewPost(it)
    }

    fun view(id:Int){
        requestView.value = id
    }

    fun observeView() : LiveData<Resource<String>> {
        return responseView
    }

    //share count
    private val requestShareCount = MutableLiveData<Int>()
    private var responseShareCount : LiveData<Resource<Response<ResponseBody>>> = Transformations.switchMap(requestShareCount){
        communityRepository.shareCount(it)
    }

    fun shareCount(id:Int){
        requestShareCount.value = id
    }

    fun observeShareCount() : LiveData<Resource<Response<ResponseBody>>> {
        return responseShareCount
    }

    //report
    private val requestReport = MutableLiveData<Int>()
    private var responseReport : LiveData<Resource<Post>> = Transformations.switchMap(requestReport){
        communityRepository.report(it)
    }

    fun report(id:Int){
        requestReport.value = id
    }

    fun observeReport() : LiveData<Resource<Post>> {
        return responseReport
    }

    //delete
    private val requestDelete = MutableLiveData<Int>()
    private var responseDelete : LiveData<Resource<Post>> = Transformations.switchMap(requestDelete){
        communityRepository.delete(it)
    }

    fun delete(id:Int){
        requestDelete.value = id
    }

    fun observeDelete() : LiveData<Resource<Post>> {
        return responseDelete
    }
}