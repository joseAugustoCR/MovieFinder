package com.example.app.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.example.app.R
import com.example.app.SessionManager
import com.example.app.di.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    val navController:NavController by lazy { findNavController(this, R.id.navHostFragment) }
    private lateinit var viewModel: MainViewModel
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, providerFactory).get(MainViewModel::class.java)
    }





}
