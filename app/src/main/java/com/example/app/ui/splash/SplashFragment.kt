package com.example.app.ui.splash

import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.app.R
import com.example.app.base.BaseFragment
import com.example.app.di.ViewModelProviderFactory
import com.example.app.ui.main.LOGIN_REGULAR
import com.example.app.utils.Constants
import com.example.app.utils.INTRO
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
        with(bgImg){
            animate()
                .alpha(1f)
                .withEndAction {

                    try {
                        with(logo){
                            animate()
                                .alpha(1f)
                                .translationY(50.px().toFloat())
                                .withEndAction{
                                    try {
                                        if(Constants.LOGIN_REQUIRED && sessionManager.isLogged() == false){
                                            safeNavigate(navController, SplashFragmentDirections.actionSplashFragmentToAuthFragment(
                                                LOGIN_REGULAR))

                                        }else{
                                            safeNavigate(navController, SplashFragmentDirections.actionSplashFragmentToMainFragment())
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                                .duration = 700
                        }
                    } catch (e: Exception) {
                    }


                }
                .duration = 400
        }
    }

    override fun onStop() {
        super.onStop()
        bgImg.clearAnimation()
        bgImg?.animation?.cancel()
        logo.clearAnimation()
        logo?.animation?.cancel()
        bgImg.alpha = 0f
        logo.alpha = 0f
        handler.removeCallbacksAndMessages(null)
    }

}
