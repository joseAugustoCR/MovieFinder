package com.example.moviefinder.ui

import android.os.Bundle
import android.util.Log.d
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviefinder.R
import com.example.moviefinder.networking.Resource
import com.example.moviefinder.utils.ViewModelProviderFactory
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    val navController:NavController by lazy { findNavController(this, R.id.navHostFragment) }
    private lateinit var viewModel: MainViewModel
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, providerFactory).get(MainViewModel::class.java)
    }


    fun setUpBottomNavigation(){
        //setup the bottom navigation
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        //this will automatically handle the toolbar
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.discoverFragment
            )
            .build()

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.discoverFragment -> {
//                    hideBottomNavigation()
                    toolbar.title = destination.label
                }
                else ->{
//                    showBottomNavigation()
                    toolbar.title = destination.label

                }
            }
        }
    }


    fun hideToolbar(){
        with(appBar){
            animate()
                .alpha(0f)
                .withEndAction { visibility = View.GONE }
                .duration = 0
        }
    }

    fun showToolbar(){
        with(appBar){
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .duration = 300
        }
    }

    fun hideBottomNavigation(){
        with(bottomNavigationView){
            animate()
                .alpha(0f)
                .withEndAction { visibility = View.GONE }
                .duration = 0
        }
    }

    fun showBottomNavigation(){
        with(bottomNavigationView){
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .duration = 300
        }
    }






//    fun subscriveObservers(){
//        viewModel.observeDiscover().removeObservers(this)
//        viewModel.observeDiscover().observe(this, Observer {
//            d("main", Gson().toJson(it))
//            when(it.status){
//                Resource.Status.SUCCESS ->{
//                    var adapter = MoviesAdapter()
//                    adapter.submitList(it.data?.results!!)
//                    recyclerView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
//                    recyclerView.adapter = adapter
//
//
//                }
//                Resource.Status.LOADING ->{
//
//                }
//                Resource.Status.ERROR ->{
//
//                }
//
//            }
//        })
//    }


    fun setAdapter(){

    }


}
