package com.example.moviefinder.ui

import androidx.lifecycle.ViewModel
import com.example.moviefinder.networking.Api
import javax.inject.Inject

class MainViewModel @Inject constructor(var api:Api) : ViewModel() {

}