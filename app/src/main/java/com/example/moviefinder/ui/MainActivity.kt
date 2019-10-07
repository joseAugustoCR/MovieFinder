package com.example.moviefinder.ui

import android.os.Bundle
import android.util.Log.d
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviefinder.R
import com.example.moviefinder.networking.Resource
import com.example.moviefinder.utils.ViewModelProviderFactory
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, providerFactory).get(MainViewModel::class.java)
        subscriveObservers()
    }


    fun subscriveObservers(){
        viewModel.observeDiscover().removeObservers(this)
        viewModel.observeDiscover().observe(this, Observer {
            d("main", Gson().toJson(it))
            when(it.status){
                Resource.Status.SUCCESS ->{
                    var adapter = DiscoverMoviesAdapter()
                    adapter.submitList(it.data?.results!!)
                    recyclerView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
                    recyclerView.adapter = adapter


                }
                Resource.Status.LOADING ->{

                }
                Resource.Status.ERROR ->{

                }

            }
        })
    }


    fun setAdapter(){

    }


}
