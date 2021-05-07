package com.example.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import okhttp3.*
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(private val api: Api, private val sharedPreferencesManager: SharedPreferencesManager){

    fun getCommunityPosts(offset:Int) : LiveData<Resource<ArrayList<Post?>>> {
        val data = MediatorLiveData<Resource<ArrayList<Post?>>>()
        data.value = Resource.loading()

        val source = api.getCommunityPosts(offset = offset.toString())
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

    fun loadMore(offset:Int) : LiveData<Resource<ArrayList<Post?>>> {
        val data = MediatorLiveData<Resource<ArrayList<Post?>>>()
        data.value = Resource.loading()

        val source = api.getCommunityPosts(offset = offset.toString())
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

    fun getPostDetail(id:Int) : LiveData<Resource<Post>> {
        val data = MediatorLiveData<Resource<Post>>()
        data.value = Resource.loading()

        val source = api.postDetail(id.toString())
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

    fun createPost(request:Post) : LiveData<Resource<Post>> {
        val data = MediatorLiveData<Resource<Post>>()
        data.value = Resource.loading()

        val source = api.createPost(request)
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

    fun postComment(request:PostComment) : LiveData<Resource<PostComment>> {
        val data = MediatorLiveData<Resource<PostComment>>()
        data.value = Resource.loading()

        val source = api.comment(id = request.id.toString(), request = request)
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

    fun like(id:Int) : LiveData<Resource<Post>> {
        val data = MediatorLiveData<Resource<Post>>()
        data.value = Resource.loading()

        val source = api.likePost(id.toString())
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

    fun viewPost(id:Int) : LiveData<Resource<String>> {
        val data = MediatorLiveData<Resource<String>>()
        data.value = Resource.loading()

        val source = api.viewPost(id.toString())
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

    fun viewPosts(request: PostsViewRequest) : LiveData<Resource<String>> {
        val data = MediatorLiveData<Resource<String>>()
        data.value = Resource.loading()

        val source = api.viewPosts(request)
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

    fun shareCount(id:Int) : LiveData<Resource<Response<ResponseBody>>> {
        val data = MediatorLiveData<Resource<Response<ResponseBody>>>()
        data.value = Resource.loading()

        val source = api.shareCount(id.toString())
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

    fun report(id:Int) : LiveData<Resource<Post>> {
        val data = MediatorLiveData<Resource<Post>>()
        data.value = Resource.loading()

        val source = api.reportPost(id.toString())
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

    fun delete(id:Int) : LiveData<Resource<Post>> {
        val data = MediatorLiveData<Resource<Post>>()
        data.value = Resource.loading()

        val source = api.deletePost(id.toString())
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

    fun unlike(id:Int) : LiveData<Resource<Any>> {
        val data = MediatorLiveData<Resource<Any>>()
        data.value = Resource.loading()

        val source = api.unlikePost(id.toString())
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