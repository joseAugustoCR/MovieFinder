package com.example.app.ui.splash

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.app.R
import com.example.app.base.BaseFragment
import com.example.app.di.ViewModelProviderFactory
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
            navController.navigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())

        }, 500)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

}
