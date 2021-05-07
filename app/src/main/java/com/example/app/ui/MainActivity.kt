package com.example.app.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.amazonaws.mobile.client.AWSMobileClient
import com.example.app.R
import com.example.app.SessionManager
import com.example.app.api.AuthResource
import com.example.app.api.PostsViewRequest
import com.example.app.api.Resource
import com.example.app.api.User
import com.example.app.di.ViewModelProviderFactory
import com.example.app.utils.CONFIGS
import com.example.app.utils.SharedPreferencesManager
import com.example.app.utils.VIEWED_POSTS
import com.github.ajalt.timberkt.d
import com.google.gson.Gson
import com.onesignal.OneSignal
import dagger.android.support.DaggerAppCompatActivity
import org.json.JSONObject
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    val navController:NavController by lazy { findNavController(this, R.id.navHostFragment) }
    private lateinit var viewModel: MainViewModel
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var sessionManager: SessionManager
    @Inject
    lateinit var sharedPreferences: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, providerFactory).get(MainViewModel::class.java)
        initAWS()
        setObservers()
        getConfigs()
        checkViewPosts()
    }

    fun getConfigs(){
        viewModel.getConfigs()
    }

    fun checkViewPosts(){
        val request = sharedPreferences.getObject(VIEWED_POSTS, PostsViewRequest::class.java)
        if(request != null && request?.posts.isNullOrEmpty() == false && sessionManager.isLogged()){
            viewModel.view(request)
        }

    }

    override fun onResume() {
        super.onResume()
        getUser()
    }


    fun initAWS(){
        AWSMobileClient.getInstance().initialize(this) {
            d{"AWS initialized!"}
        }.execute()
    }

    fun setObservers(){
        viewModel.observeView().observe(this, Observer {
            if(it.status == Resource.Status.SUCCESS) {
                sharedPreferences.putObject(VIEWED_POSTS, PostsViewRequest(posts = arrayListOf()))
                d{"view success"}
            }
        })

        viewModel.observeUser().observe(this, Observer {
            if(it.status == Resource.Status.SUCCESS) {
                sessionManager.updateUser(it.data)
                d{"user updated " + it.data.toString()}
            }
        })

        viewModel.observeConfigs().observe(this, Observer {
            if(it.status == Resource.Status.SUCCESS) {
                sessionManager.setConfigs(it.data)
            }
        })

        sessionManager.getAuthUser().observe(this, Observer {
            d{"user observe " + it.data.toString()}
            if(it.status == AuthResource.AuthStatus.NOT_AUTHENTICATED){
                deleteOneSignalTags()

            }else if(it.status == AuthResource.AuthStatus.AUTHENTICATED){
                sendOneSignalTags(it.data)

            }
        })

    }


    fun deleteOneSignalTags() {
        try {
            OneSignal.removeExternalUserId()
            OneSignal.getTags { tags: JSONObject? ->
                if (tags == null) return@getTags
                d { "tags = " + Gson().toJson(tags) }

                var mTags =
                    JSONObject(Gson().toJson(tags).toString()).getJSONObject("nameValuePairs")
                d { "mTags = " + Gson().toJson(mTags) }

                val keys = mTags.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    d { "**********" }
                    val innerJObject = mTags.getString(key)
                    d { "key = $key value = $innerJObject" }
                    OneSignal.deleteTag(key)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun sendOneSignalTags(user: User?) {
        if(user == null) return
        try {
            OneSignal.setExternalUserId(user.id.toString())
            OneSignal.getTags { tags: JSONObject? ->
                d { "tags = " + Gson().toJson(tags) }

                if (tags == null){
                    OneSignal.sendTag("user_id", user.id.toString())
                    d { "adding key userid" }
                    user?.tags?.forEach {
                        OneSignal.sendTag(it.key, it.value)
                        d { "adding key = $it.key" }
                    }

                }else{
                    var mTags =
                        JSONObject(Gson().toJson(tags).toString()).getJSONObject("nameValuePairs")
                    d { "mTags = " + Gson().toJson(mTags) }

                    val keys = mTags.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val innerJObject = mTags.getString(key)
                        d { "deleting key = $key" }
                        OneSignal.deleteTag(key)
                    }
                    d { "**********" }

                    OneSignal.sendTag("user_id", user.id.toString())
                    d { "adding key userid" }
                    user?.tags?.forEach {
                        OneSignal.sendTag(it.key, it.value)
                        d { "adding key = $it.key" }
                    }
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getUser(){
        if(sessionManager.isLogged()){
            viewModel.getUser(sessionManager.getUserID() ?: 0)
        }
    }



}
