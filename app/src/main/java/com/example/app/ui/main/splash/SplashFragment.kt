package com.example.app.ui.main.splash

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.app.R
import com.example.app.base.BaseFragment
import com.example.app.di.ViewModelProviderFactory
import com.example.app.utils.extensions.px
import kotlinx.android.synthetic.main.splash_fragment.*
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
        try {
            with(logo){
                animate()
                    .alpha(1f)
                    .translationY(70.px().toFloat())
                    .withEndAction{
                        try {
                            navController.navigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())

                        } catch (e: Exception) {
                        }
                    }
                    .duration = 1800
            }
        } catch (e: Exception) {
        }

    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

}
