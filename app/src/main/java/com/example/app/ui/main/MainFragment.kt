package com.example.app.ui.main


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

import com.example.app.R
import com.example.app.SessionManager
import com.example.app.api.NetworkStatus
import com.example.app.base.BaseFragment
import com.example.app.base.NAVIGATION_RESULT_OK
import com.example.app.ui.main.timeline.TimelineFragment
import com.example.app.utils.extensions.hideKeyboard
import com.example.app.utils.extensions.setupWithNavController
import com.example.app.utils.navigation.NavigationResult
import com.example.app.utils.navigation.NavigationResultListener
import com.example.daggersample.networking.NetworkEvent
import com.github.ajalt.timberkt.d
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
val LOGIN_REGULAR = 0
class MainFragment : BaseFragment(), NavigationResultListener {
    val navOptions = NavOptions.Builder()
        .build()

    override fun onNavigationResult(result: NavigationResult) {
        d{result.toString()}
    }

    lateinit var mainNavHostFragment: NavHostFragment
    lateinit var mainNavController:NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomNavigation()

    }

    override fun onResume() {
        super.onResume()
        NetworkEvent.register(viewLifecycleOwner, Consumer {

            if(it == NetworkStatus.UNAUTHORIZED){


                if(sessionManager.isLogged()){
                }
            }
        })
    }

    override fun onStop() {
        NetworkEvent.unregister(viewLifecycleOwner)
        super.onStop()
    }

    fun init(){

    }

    fun setUpBottomNavigation(){
        // since I'm using a nested fragment
        mainNavHostFragment = childFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        mainNavController = mainNavHostFragment.navController
        //setup the bottom navigation
        val navGraphsIds = listOf(R.id.timelineFragment)
        NavigationUI.setupWithNavController(bottomNavigationView, mainNavController)

        //this will automatically handle the toolbar
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.timelineFragment
            )
            .build()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            if(isValidDestination(it.itemId) == false){
                if(it.itemId == R.id.timelineFragment){
                    val timelineFragment = mainNavHostFragment.childFragmentManager.fragments.get(0)
                    if(timelineFragment != null && timelineFragment is TimelineFragment){
//                        (timelineFragment as TimelineFragment).scrollToTop()
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                return@setOnNavigationItemSelectedListener false
            }



            when(it.itemId){
                R.id.timelineFragment->{
                    if(mainNavController.popBackStack(R.id.timelineFragment, false)){

                    }else {
                        mainNavController.navigate(R.id.timelineFragment, null, navOptions)
                    }
                }
            }

            return@setOnNavigationItemSelectedListener true
        }

        mainNavController.addOnDestinationChangedListener { controller, destination, arguments ->
            requireActivity().hideKeyboard()

            when(destination.id){
                R.id.timelineFragment ->{
                    showBottomNavigation()
                }

                else ->{
                    hideBottomNavigation()
                }
            }
        }
    }


    fun isValidDestination(destination:Int):Boolean{
        return destination != mainNavController.currentDestination?.id
    }




    fun goToLogin(type:Int){
        safeNavigate(navController, MainFragmentDirections.actionMainFragmentToAuthFragment())
    }

    fun goToTimeline(){
        mainNavController.navigate(R.id.timelineFragment, null, navOptions)
    }

    fun hideBottomNavigation(){
        with(bottomNavigationView){
            animate()
                .alpha(0f)
                .withEndAction { visibility = android.view.View.GONE }
                .duration = 0
        }
    }

    fun showBottomNavigation(){
        with(bottomNavigationView){
            visibility = android.view.View.VISIBLE
            animate()
                .alpha(1f)
                .duration = 300
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        d{"result on main fragment"}
    }


}
