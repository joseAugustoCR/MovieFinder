package com.example.moviefinder.ui.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_bottom_nav.*

/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragment : BaseFragment() {
    lateinit var navHostFragment: NavHostFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_bottom_nav, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomNavigation()
    }

    fun setUpBottomNavigation(){
        // since I'm using a nested fragment
        navHostFragment = childFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        //setup the bottom navigation
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)

        //this will automatically handle the toolbar
//        val appBarConfiguration = AppBarConfiguration
//            .Builder(
//                R.id.feedFragment,
//                R.id.profileFragment
//            )
//            .build()
//
//        NavigationUI.setupWithNavController(toolbar, navHostFragment.navController, appBarConfiguration)
//
//        navHostFragment.navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            when(destination.id){
//                R.id.settingsFragment -> {
//                    hideBottomNavigation()
//                    toolbar.title = destination.label
//                }
//                R.id.feedDetailFragment ->{
//                    toolbar.title = arguments?.getString("title")
//                }
//                else ->{
//                    showBottomNavigation()
//                    toolbar.title = destination.label
//
//                }
//            }
//        }

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



}
