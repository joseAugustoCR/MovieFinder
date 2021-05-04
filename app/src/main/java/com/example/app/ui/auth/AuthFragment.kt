package com.example.app.ui.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs

import com.example.app.R
import com.example.app.base.BaseFragment
import com.example.app.base.REQUEST_LOGIN
import com.example.app.utils.navigation.NavigationResult

/**
 * A simple [Fragment] subclass.
 */
class AuthFragment : BaseFragment() {
    val args:AuthFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }


    fun setAuthNavigationResult(result:NavigationResult){
        setNavigationResult(result)
    }

    fun finishAuth(){
        navController.popBackStack()
    }

}
