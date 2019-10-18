package com.example.moviefinder.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.moviefinder.networking.Api
import com.example.moviefinder.networking.DiscoverMovieResponse
import com.example.moviefinder.networking.Resource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(var api:Api) : ViewModel() {

}