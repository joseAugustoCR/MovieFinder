package com.example.moviefinder.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.moviefinder.R
import com.example.moviefinder.ui.moviedetails.MovieDetailsFragmentArgs
import com.example.moviefinder.utils.ViewModelProviderFactory
import com.example.moviefinder.utils.hideKeyboard
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
        setUpBottomNavigation()
    }


    fun setUpBottomNavigation(){
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.moviesFragment -> {
                    this@MainActivity.hideKeyboard()
                    showBottomNavigation()
                }
                R.id.TVShowsFragment->{
                    this@MainActivity.hideKeyboard()
                    showBottomNavigation()
                }
                R.id.movieDetailsFragment ->{
                    arguments?.let {
                        val args = MovieDetailsFragmentArgs.fromBundle(arguments)
                    }
                    hideBottomNavigation()

                }
                else ->{
                    hideBottomNavigation()

                }
            }
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
//                    var moviesAdapter = MoviesAdapter()
//                    moviesAdapter.submitList(it.data?.results!!)
//                    recyclerView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
//                    recyclerView.moviesAdapter = moviesAdapter
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
