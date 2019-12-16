package com.example.app.ui

import androidx.lifecycle.ViewModel
import com.example.app.api.Api
import javax.inject.Inject

class MainViewModel @Inject constructor(var api:Api) : ViewModel() {

}