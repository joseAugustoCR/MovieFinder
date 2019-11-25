package com.example.moviefinder.ui.main.splash

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.di.ViewModelProviderFactory
import javax.inject.Inject

class SplashFragment : BaseFragment() {

    @Inject lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: SplashViewModel
    val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(SplashViewModel::class.java)
    }


    override fun onResume() {
        super.onResume()
        handler.postDelayed({
            navController.navigate(SplashFragmentDirections.actionSplashFragmentToBottomNavFragment())

        }, 500)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

}
