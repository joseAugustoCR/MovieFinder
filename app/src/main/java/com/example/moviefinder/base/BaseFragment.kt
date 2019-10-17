package com.example.moviefinder.base

import androidx.navigation.Navigation
import dagger.android.support.DaggerFragment

open class BaseFragment : DaggerFragment() {
    val navController by lazy { Navigation.findNavController(view!!) }

}